CREATE TABLE evento_rider_items (
    evento_id UUID NOT NULL REFERENCES evento(id) ON DELETE CASCADE,
    nome_equipamento VARCHAR(255) NOT NULL,
    quantidade INT NOT NULL
);
