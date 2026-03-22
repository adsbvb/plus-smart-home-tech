CREATE TABLE IF NOT EXISTS products (
    product_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    product_name VARCHAR(255) NOT NULL,
    description TEXT NOT NULL,
    image_src VARCHAR(500),
    quantity_state VARCHAR(20) NOT NULL CHECK (quantity_state IN ('ENDED', 'FEW', 'ENOUGH', 'MANY')),
    product_state VARCHAR(20) NOT NULL CHECK (product_state IN ('ACTIVE', 'DEACTIVATE')),
    product_category VARCHAR(20) NOT NULL CHECK (product_category IN ('LIGHTING', 'CONTROL', 'SENSORS')),
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 1),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);