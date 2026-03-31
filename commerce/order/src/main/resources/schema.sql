CREATE TABLE IF NOT EXISTS orders (
    order_id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    username VARCHAR(50) NOT NULL,
    order_state VARCHAR(20) NOT NULL DEFAULT 'NEW' CHECK (order_state IN (
        'NEW', 'ON_PAYMENT', 'ON_DELIVERY', 'DONE', 'DELIVERED', 'ASSEMBLED', 'PAID', 'COMPLETED', 'DELIVERY_FAILED',
        'ASSEMBLY_FAILED', 'PAYMENT_FAILED', 'PRODUCT_RETURNED', 'CANCELED')),
    shopping_cart_id UUID,
    delivery_id UUID,
    payment_id UUID,
    delivery_volume DECIMAL(10, 2),
    delivery_weight DECIMAL(10, 2),
    fragile BOOLEAN DEFAULT FALSE,
    delivery_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    product_price DECIMAL(10, 2) NOT NULL DEFAULT 0,
    total_price DECIMAL(10, 2) GENERATED ALWAYS AS (products_price + delivery_price) STORED,
);

CREATE TABLE IF NOT EXISTS order_products (
    id UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    order_id UUID NOT NULL,
    product_id UUID NOT NULL,
    quantity BIGINT NOT NULL CHECK (quantity > 0),
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    CONSTRAINT unique_order_product UNIQUE (order_id, product_id)
);