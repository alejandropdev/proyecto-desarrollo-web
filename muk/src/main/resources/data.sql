-- =========================
-- CATEGORIAS
-- =========================

INSERT INTO categorias (id, nombre) VALUES
(1,'BBQ'),
(2,'BURGERS'),
(3,'CHICKEN'),
(4,'DESSERTS'),
(5,'DRINKS');

-- =========================
-- CLIENTES
-- =========================

INSERT INTO clientes (nombre, apellido, email, telefono, direccion, password) VALUES
('Sara', 'Munoz', 'sara@muk.com', '3001111111', 'Bogota', '1234'),
('Juan', 'Perez', 'juan@muk.com', '3001111112', 'Bogota', '1234'),
('Laura', 'Gomez', 'laura@muk.com', '3001111113', 'Bogota', '1234'),
('Mateo', 'Ruiz', 'mateo@muk.com', '3001111114', 'Bogota', '1234'),
('Ana', 'Torres', 'ana@muk.com', '3001111115', 'Bogota', '1234'),
('Carlos', 'Lopez', 'carlos@muk.com', '3001111116', 'Bogota', '1234'),
('Valentina', 'Rojas', 'valentina@muk.com', '3001111117', 'Bogota', '1234'),
('Andres', 'Castro', 'andres@muk.com', '3001111118', 'Bogota', '1234'),
('Camila', 'Vargas', 'camila@muk.com', '3001111119', 'Bogota', '1234'),
('Daniel', 'Moreno', 'daniel@muk.com', '3001111120', 'Bogota', '1234');

-- =========================
-- PRODUCTOS / COMIDAS
-- =========================

INSERT INTO productos (nombre,categoria_id,precio,imagen_url,descripcion) VALUES
('Muk BBQ Ribs',1,42000,'https://images.unsplash.com/photo-1544025162-d76694265947','Costillas BBQ cocidas lentamente.'),
('BBQ Smash Burger',2,32000,'https://images.unsplash.com/photo-1568901346375-23c9450c58cd','Hamburguesa smash con salsa BBQ.'),
('Muk Chicken Crunch',3,28000,'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58','Pollo crujiente con especias.'),
('Chocolate Lava Muk',4,17000,'https://images.unsplash.com/photo-1578985545062-69928b1d9587','Postre de chocolate fundido.'),
('Muk Lemonade',5,9000,'https://images.unsplash.com/photo-1513558161293-cdaf765ed2fd','Limonada natural refrescante'),

('BBQ Mega Ribs',1,45000,'https://images.unsplash.com/photo-1558030006-450675393462','Costillas BBQ tamaño gigante.'),
('Double Muk Burger',2,35000,'https://images.unsplash.com/photo-1550547660-d9450f859349','Doble carne y doble queso.'),
('Chicken Fire Wings',3,26000,'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d','Alitas picantes estilo Muk.'),
('Muk Brownie Tower',4,18000,'https://images.unsplash.com/photo-1606313564200-e75d5e30476c','Brownie con helado.'),
('Muk Mango Drink',5,10000,'https://images.unsplash.com/photo-1600271886742-f049cd451bba','Bebida natural de mango'),

('BBQ Pulled Pork',1,34000,'https://images.unsplash.com/photo-1600891964599-f61ba0e24092','Cerdo BBQ desmechado.'),
('Muk Classic Burger',2,27000,'https://images.unsplash.com/photo-1550547660-d9450f859349','Hamburguesa clásica Muk.'),
('Chicken Gold Nuggets',3,24000,'https://images.unsplash.com/photo-1562967916-eb82221dfb36','Nuggets crujientes.'),
('Muk Cheesecake',4,16000,'https://images.unsplash.com/photo-1551024601-bec78aea704b','Cheesecake de la casa.'),
('Muk Cola Drink',5,8000,'https://images.unsplash.com/photo-1581006852262-e4307cf6283a','Refresco clásico'),

('BBQ Ribs Challenge',1,48000,'https://images.unsplash.com/photo-1544025162-d76694265947','Reto Muk de costillas.'),
('Triple Muk Burger',2,38000,'https://images.unsplash.com/photo-1550317138-10000687a72b','Triple carne Muk.'),
('Chicken Muk Sandwich',3,26000,'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d','Sandwich de pollo.'),
('Muk Choco Bomb',4,19000,'https://images.unsplash.com/photo-1551024601-bec78aea704b','Explosión de chocolate.'),
('Muk Orange Juice',5,9000,'https://images.unsplash.com/photo-1542444459-db63c3a0a3a3','Jugo de naranja'),

('BBQ Beef Plate',1,36000,'https://images.unsplash.com/photo-1544025162-d76694265947','Carne BBQ Muk.'),
('Muk Bacon Burger',2,33000,'https://images.unsplash.com/photo-1550317138-10000687a72b','Hamburguesa con bacon.'),
('Chicken Muk Bucket',3,39000,'https://images.unsplash.com/photo-1626645738196-c2a7c87a8f58','Bucket de pollo Muk.'),
('Muk Strawberry Cake',4,17000,'https://images.unsplash.com/photo-1551024601-bec78aea704b','Pastel de fresa.'),
('Muk Soda Drink',5,7000,'https://images.unsplash.com/photo-1581006852262-e4307cf6283a','Soda refrescante'),

('BBQ Muk Plate XL',1,52000,'https://images.unsplash.com/photo-1544025162-d76694265947','Plato BBQ extra grande.'),
('Muk Double Cheese',2,34000,'https://images.unsplash.com/photo-1550547660-d9450f859349','Doble queso Muk.'),
('Chicken Muk Tenders',3,25000,'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d','Tenders crujientes.'),
('Muk Ice Cream',4,14000,'https://images.unsplash.com/photo-1563805042-7684c019e1cb','Helado Muk.'),
('Muk Pineapple Drink',5,9000,'https://images.unsplash.com/photo-1600271886742-f049cd451bba','Bebida de piña'),

('BBQ Muk Grill',1,41000,'https://images.unsplash.com/photo-1544025162-d76694265947','Parrilla Muk.'),
('Muk Monster Burger',2,39000,'https://images.unsplash.com/photo-1550317138-10000687a72b','Hamburguesa monstruo Muk.'),
('Chicken Muk Hot',3,27000,'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d','Pollo picante Muk.'),
('Muk Choco Cake',4,16000,'https://images.unsplash.com/photo-1551024601-bec78aea704b','Pastel chocolate Muk.'),
('Muk Berry Drink',5,10000,'https://images.unsplash.com/photo-1600271886742-f049cd451bba','Bebida de frutos rojos');

INSERT INTO productos (nombre, categoria_id, precio, imagen_url, descripcion) VALUES
('Muk BBQ Deluxe',1,43000,'https://images.unsplash.com/photo-1544025162-d76694265947','Costillas BBQ estilo Muk.'),
('Muk Ultimate Burger',2,37000,'https://images.unsplash.com/photo-1550547660-d9450f859349','Hamburguesa premium Muk.'),
('Muk Chicken Supreme',3,29000,'https://images.unsplash.com/photo-1604908176997-125f25cc6f3d','Pollo crujiente especial Muk.'),
('Muk Chocolate Dream',4,18000,'https://images.unsplash.com/photo-1551024601-bec78aea704b','Postre de chocolate Muk.'),
('Muk Tropical Drink',5,10000,'https://images.unsplash.com/photo-1600271886742-f049cd451bba','Bebida tropical refrescante.');