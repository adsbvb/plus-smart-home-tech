CREATE TABLE IF NOT EXISTS delivery (
    delivery_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    delivery_volume DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (delivery_volume >= 0),
    delivery_weight DECIMAL(10, 2) NOT NULL DEFAULT 0 CHECK (delivery_weight >= 0),
    fragile BOOLEAN NOT NULL DEFAULT FALSE,
    from_address_id UUID NOT NULL,
    to_address_id UUID NOT NULL,
    order_id UUID NOT NULL,
    delivery_state VARCHAR(20) NOT NULL DEFAULT 'CREATED',
    CONSTRAINT delivery_from_address_fk FOREIGN KEY (from_address_id) REFERENCES address(id) ON DELETE CASCADE,
    CONSTRAINT delivery_to_address_fk FOREIGN KEY (to_address_id) REFERENCES address(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS address (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    country VARCHAR(50),
    city VARCHAR(50),
    street VARCHAR(50),
    house VARCHAR(50),
    flat VARCHAR(50)
);