ALTER TABLE cell_union
    ALTER COLUMN creator_id TYPE DECIMAL(40) USING (creator_id::DECIMAL(40));