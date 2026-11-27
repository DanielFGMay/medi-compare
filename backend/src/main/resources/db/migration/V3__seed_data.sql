-- Migración V3: Datos iniciales para pruebas
-- Categorías de productos
INSERT INTO category (id, name, code, is_active, created_at) VALUES
    ('a1111111-1111-1111-1111-111111111111', 'Medicamentos', 'MED', true, CURRENT_TIMESTAMP),
    ('a2222222-2222-2222-2222-222222222222', 'Productos OTC', 'OTC', true, CURRENT_TIMESTAMP),
    ('a3333333-3333-3333-3333-333333333333', 'Belleza y Higiene', 'BEAU', true, CURRENT_TIMESTAMP);

-- Cadena Farmacias Guadalajara
INSERT INTO chain (id, name, logo_url, website, is_active, created_at, updated_at) VALUES
    ('b1111111-1111-1111-1111-111111111111', 'Farmacias Guadalajara', 'https://via.placeholder.com/100', 'https://www.farmaciasguadalajara.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cadena Farmacias del Ahorro
INSERT INTO chain (id, name, logo_url, website, is_active, created_at, updated_at) VALUES
    ('b2222222-2222-2222-2222-222222222222', 'Farmacias del Ahorro', 'https://via.placeholder.com/100', 'https://www.farmaciadelahorro.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Cadena Farmacias Similares
INSERT INTO chain (id, name, logo_url, website, is_active, created_at, updated_at) VALUES
    ('b3333333-3333-3333-3333-333333333333', 'Farmacias Similares', 'https://via.placeholder.com/100', 'https://www.farmaciasimilares.com', true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sucursales de Farmacias Guadalajara (Villahermosa)
INSERT INTO branch (id, chain_id, name, address, latitude, longitude, is_active, created_at, updated_at) VALUES
    ('c1111111-1111-1111-1111-111111111111', 'b1111111-1111-1111-1111-111111111111', 'Sucursal Central GH', 'Av. Reforma 123, Villahermosa', 17.9869, -92.9303, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('c2222222-2222-2222-2222-222222222222', 'b1111111-1111-1111-1111-111111111111', 'Sucursal Catedral', 'Calle Catedral 456, Villahermosa', 17.9850, -92.9250, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sucursales de Farmacias del Ahorro (Cunduacán)
INSERT INTO branch (id, chain_id, name, address, latitude, longitude, is_active, created_at, updated_at) VALUES
    ('c3333333-3333-3333-3333-333333333333', 'b2222222-2222-2222-2222-222222222222', 'Sucursal Central FaA', 'Blvd. Revolución 789, Cunduacán', 18.1167, -92.6000, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Sucursales de Farmacias Similares (Villahermosa)
INSERT INTO branch (id, chain_id, name, address, latitude, longitude, is_active, created_at, updated_at) VALUES
    ('c4444444-4444-4444-4444-444444444444', 'b3333333-3333-3333-3333-333333333333', 'Sucursal Central FS', 'Calle Hidalgo 321, Villahermosa', 17.9900, -92.9350, true, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Productos
INSERT INTO product (id, name, category_id, is_rx, strength, form, created_at, updated_at) VALUES
    ('d1111111-1111-1111-1111-111111111111', 'Paracetamol 500mg', 'a1111111-1111-1111-1111-111111111111', false, '500mg', 'Tabletas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d2222222-2222-2222-2222-222222222222', 'Ibuprofeno 400mg', 'a1111111-1111-1111-1111-111111111111', false, '400mg', 'Tabletas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d3333333-3333-3333-3333-333333333333', 'Dipirona 500mg', 'a1111111-1111-1111-1111-111111111111', false, '500mg', 'Tabletas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d4444444-4444-4444-4444-444444444444', 'Vitamin C 1000mg', 'a2222222-2222-2222-2222-222222222222', false, '1000mg', 'Cápsulas', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('d5555555-5555-5555-5555-555555555555', 'Crema Hidratante', 'a3333333-3333-3333-3333-333333333333', false, null, 'Tubo', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Ofertas de precios (productos genéricos y de prueba)
-- Paracetamol en Farmacias Guadalajara
INSERT INTO offer (id, product_id, branch_id, chain_id, price, price_promo, promo_text, product_url, scraped_at, created_at) VALUES
    ('e1111111-1111-1111-1111-111111111111', 'd1111111-1111-1111-1111-111111111111', 'c1111111-1111-1111-1111-111111111111', 'b1111111-1111-1111-1111-111111111111', 8.50, 7.50, '2x1 en presentaciones de 20 unidades', 'https://www.farmaciasguadalajara.com/paracetamol', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('e2222222-2222-2222-2222-222222222222', 'd1111111-1111-1111-1111-111111111111', 'c2222222-2222-2222-2222-222222222222', 'b1111111-1111-1111-1111-111111111111', 8.00, null, null, 'https://www.farmaciasguadalajara.com/paracetamol-succinato', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Paracetamol en Farmacias del Ahorro
INSERT INTO offer (id, product_id, branch_id, chain_id, price, price_promo, promo_text, product_url, scraped_at, created_at) VALUES
    ('e3333333-3333-3333-3333-333333333333', 'd1111111-1111-1111-1111-111111111111', 'c3333333-3333-3333-3333-333333333333', 'b2222222-2222-2222-2222-222222222222', 7.80, 6.99, 'Envío gratis en tienda', 'https://www.farmaciadelahorro.com/paracetamol', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Paracetamol en Farmacias Similares
INSERT INTO offer (id, product_id, branch_id, chain_id, price, price_promo, promo_text, product_url, scraped_at, created_at) VALUES
    ('e4444444-4444-4444-4444-444444444444', 'd1111111-1111-1111-1111-111111111111', 'c4444444-4444-4444-4444-444444444444', 'b3333333-3333-3333-3333-333333333333', 9.20, 8.50, 'Descuento de 10% en compras mayores a $500', 'https://www.farmaciasimilares.com/paracetamol', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Ibuprofeno en Farmacias Guadalajara
INSERT INTO offer (id, product_id, branch_id, chain_id, price, price_promo, promo_text, product_url, scraped_at, created_at) VALUES
    ('e5555555-5555-5555-5555-555555555555', 'd2222222-2222-2222-2222-222222222222', 'c1111111-1111-1111-1111-111111111111', 'b1111111-1111-1111-1111-111111111111', 12.00, null, null, 'https://www.farmaciasguadalajara.com/ibuprofeno', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Vitamin C en Farmacias del Ahorro
INSERT INTO offer (id, product_id, branch_id, chain_id, price, price_promo, promo_text, product_url, scraped_at, created_at) VALUES
    ('e6666666-6666-6666-6666-666666666666', 'd4444444-4444-4444-4444-444444444444', 'c3333333-3333-3333-3333-333333333333', 'b2222222-2222-2222-2222-222222222222', 15.99, 12.99, 'Oferta especial', 'https://www.farmaciadelahorro.com/vitaminac', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Dipirona en Farmacias Similares
INSERT INTO offer (id, product_id, branch_id, chain_id, price, price_promo, promo_text, product_url, scraped_at, created_at) VALUES
    ('e7777777-7777-7777-7777-777777777777', 'd3333333-3333-3333-3333-333333333333', 'c4444444-4444-4444-4444-444444444444', 'b3333333-3333-3333-3333-333333333333', 11.50, 9.99, '50% de descuento por tiempo limitado', 'https://www.farmaciasimilares.com/dipirona', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);