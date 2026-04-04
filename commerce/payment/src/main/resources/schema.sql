CREATE TABLE IF NOT EXISTS payments (
    payment_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID NOT NULL,
    product_cost DECIMAL(10, 2) NOT NULL CHECK (product_cost >= 0),
    delivery_cost DECIMAL(10, 2) NOT NULL CHECK (delivery_cost >= 0),
    total_cost DECIMAL(10, 2) NOT NULL CHECK (total_cost >= 0),
    fee_total DECIMAL(10, 2) NOT NULL (fee_total >= 0),
    payment_state VARCHAR(20) NOT NULL DEFAULT 'PENDING'
);