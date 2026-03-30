CREATE TABLE IF NOT EXISTS warehouse_products (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_id UUID NOT NULL UNIQUE,
    quantity BIGINT NOT NULL CHECK (quantity >= 0),
    fragile BOOLEAN DEFAULT FALSE,
    width DECIMAL(10, 2),
    height DECIMAL(10, 2),
    depth DECIMAL(10, 2),
    weight DECIMAL(10, 2),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);