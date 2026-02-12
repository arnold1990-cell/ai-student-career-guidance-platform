-- Migrates users.id from bigint/integer to UUID and rewires every FK that points to users(id).
-- Safe to run on databases where users.id is already UUID (no-op).

CREATE EXTENSION IF NOT EXISTS pgcrypto;

DO $$
DECLARE
    users_id_type text;
    fk_rec record;
BEGIN
    SELECT data_type
    INTO users_id_type
    FROM information_schema.columns
    WHERE table_schema = 'public'
      AND table_name = 'users'
      AND column_name = 'id';

    IF users_id_type IS NULL THEN
        RAISE NOTICE 'Table public.users or column id does not exist, skipping migration.';
        RETURN;
    END IF;

    IF users_id_type = 'uuid' THEN
        RAISE NOTICE 'users.id is already uuid, skipping bigint->uuid migration.';
        RETURN;
    END IF;

    IF users_id_type NOT IN ('bigint', 'integer') THEN
        RAISE EXCEPTION 'Unsupported users.id type: %', users_id_type;
    END IF;

    CREATE TEMP TABLE tmp_users_fk_refs (
        constraint_name text NOT NULL,
        table_schema text NOT NULL,
        table_name text NOT NULL,
        column_name text NOT NULL
    ) ON COMMIT DROP;

    INSERT INTO tmp_users_fk_refs (constraint_name, table_schema, table_name, column_name)
    SELECT
        tc.constraint_name,
        kcu.table_schema,
        kcu.table_name,
        kcu.column_name
    FROM information_schema.table_constraints tc
    JOIN information_schema.key_column_usage kcu
      ON tc.constraint_name = kcu.constraint_name
     AND tc.table_schema = kcu.table_schema
    JOIN information_schema.constraint_column_usage ccu
      ON ccu.constraint_name = tc.constraint_name
     AND ccu.constraint_schema = tc.table_schema
    WHERE tc.constraint_type = 'FOREIGN KEY'
      AND ccu.table_schema = 'public'
      AND ccu.table_name = 'users'
      AND ccu.column_name = 'id'
    ORDER BY kcu.table_schema, kcu.table_name, kcu.column_name;

    IF NOT EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_schema = 'public'
          AND table_name = 'users'
          AND column_name = 'id_uuid'
    ) THEN
        ALTER TABLE public.users ADD COLUMN id_uuid uuid;
    END IF;

    UPDATE public.users
    SET id_uuid = COALESCE(id_uuid, gen_random_uuid());

    FOR fk_rec IN SELECT * FROM tmp_users_fk_refs
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I ADD COLUMN IF NOT EXISTS %I uuid',
            fk_rec.table_schema,
            fk_rec.table_name,
            fk_rec.column_name || '_uuid'
        );

        EXECUTE format(
            'UPDATE %I.%I child SET %I = u.id_uuid FROM public.users u WHERE child.%I = u.id',
            fk_rec.table_schema,
            fk_rec.table_name,
            fk_rec.column_name || '_uuid',
            fk_rec.column_name
        );

        EXECUTE format(
            'ALTER TABLE %I.%I DROP CONSTRAINT %I',
            fk_rec.table_schema,
            fk_rec.table_name,
            fk_rec.constraint_name
        );
    END LOOP;

    ALTER TABLE public.users DROP CONSTRAINT IF EXISTS users_pkey;
    ALTER TABLE public.users DROP COLUMN id;
    ALTER TABLE public.users RENAME COLUMN id_uuid TO id;
    ALTER TABLE public.users ALTER COLUMN id SET NOT NULL;
    ALTER TABLE public.users ADD CONSTRAINT users_pkey PRIMARY KEY (id);

    FOR fk_rec IN SELECT * FROM tmp_users_fk_refs
    LOOP
        EXECUTE format(
            'ALTER TABLE %I.%I DROP COLUMN %I',
            fk_rec.table_schema,
            fk_rec.table_name,
            fk_rec.column_name
        );

        EXECUTE format(
            'ALTER TABLE %I.%I RENAME COLUMN %I TO %I',
            fk_rec.table_schema,
            fk_rec.table_name,
            fk_rec.column_name || '_uuid',
            fk_rec.column_name
        );

        EXECUTE format(
            'ALTER TABLE %I.%I ADD CONSTRAINT %I FOREIGN KEY (%I) REFERENCES public.users(id)',
            fk_rec.table_schema,
            fk_rec.table_name,
            fk_rec.constraint_name,
            fk_rec.column_name
        );
    END LOOP;
END $$;
