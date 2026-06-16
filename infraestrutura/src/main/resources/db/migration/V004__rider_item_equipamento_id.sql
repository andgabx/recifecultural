ALTER TABLE evento_rider_items
    ADD COLUMN equipamento_id UUID,
    DROP COLUMN nome_equipamento;
