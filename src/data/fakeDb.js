import { Producto } from '../models/Food.js';

const seedFoods = [
  new Producto({
    id: 'FOOD-001',
    name: 'The Beast Burger',
    description: 'Doble carne smash, cheddar, tocino ahumado, cebolla caramelizada y salsa Muk.',
    price: 29000,
    imageUrl:
      'https://images.unsplash.com/photo-1550547660-d9450f859349?auto=format&fit=crop&w=1200&q=80',
    category: 'Burgers',
    available: true,
  }),
  new Producto({
    id: 'FOOD-002',
    name: 'Nuclear Ramen',
    description: 'Ramen picante nivel extremo con cerdo braseado, huevo y aceite de chili.',
    price: 24500,
    imageUrl:
      'https://images.unsplash.com/photo-1604908176997-125f25cc5003?auto=format&fit=crop&w=1200&q=80',
    category: 'Ramen',
    available: true,
  }),
  new Producto({
    id: 'FOOD-003',
    name: 'Titan Fried Chicken',
    description: 'Pollo crujiente XL con glaseado spicy-honey y pepinillos.',
    price: 32000,
    imageUrl:
      'https://images.unsplash.com/photo-1604909052743-94e16f8efb58?auto=format&fit=crop&w=1200&q=80',
    category: 'Chicken',
    available: true,
  }),
  new Producto({
    id: 'FOOD-004',
    name: 'Muk Loaded Fries',
    description: 'Papas fritas con queso, chili con carne, jalapeños y crema agria.',
    price: 14750,
    imageUrl:
      'https://images.unsplash.com/photo-1551024601-bec78aea704b?auto=format&fit=crop&w=1200&q=80',
    category: 'Sides',
    available: true,
  }),
  new Producto({
    id: 'FOOD-005',
    name: 'Giant BBQ Ribs',
    description: 'Costillas BBQ a fuego lento con maíz a la mantequilla y ensalada coleslaw.',
    price: 38900,
    imageUrl:
      'https://images.unsplash.com/photo-1544025162-d76694265947?auto=format&fit=crop&w=1200&q=80',
    category: 'BBQ',
    available: true,
  }),
  new Producto({
    id: 'FOOD-006',
    name: 'Cheese Volcano Nachos',
    description: 'Nachos gigantes con queso derretido, guacamole, pico de gallo y frijoles.',
    price: 18250,
    imageUrl:
      'https://images.unsplash.com/photo-1600891964092-4316c288032e?auto=format&fit=crop&w=1200&q=80',
    category: 'Mex',
    available: true,
  }),
  new Producto({
    id: 'FOOD-007',
    name: 'Korean Hot Wings',
    description: 'Alitas coreanas con gochujang, sésamo tostado y cebollín.',
    price: 21000,
    imageUrl:
      'https://images.unsplash.com/photo-1604908177522-040b643537fd?auto=format&fit=crop&w=1200&q=80',
    category: 'Chicken',
    available: false,
  }),
  new Producto({
    id: 'FOOD-008',
    name: 'Truffle Mushroom Burger',
    description: 'Carne Angus, hongos salteados, mayo de trufa y rúcula.',
    price: 27500,
    imageUrl:
      'https://images.unsplash.com/photo-1550317138-10000687a72b?auto=format&fit=crop&w=1200&q=80',
    category: 'Burgers',
    available: true,
  }),
  new Producto({
    id: 'FOOD-009',
    name: 'Chocolate Lava Cake',
    description: 'Bizcocho tibio con centro de chocolate fundido y helado de vainilla.',
    price: 9950,
    imageUrl:
      'https://images.unsplash.com/photo-1542826438-bd32f43d626f?auto=format&fit=crop&w=1200&q=80',
    category: 'Desserts',
    available: true,
  }),
  new Producto({
    id: 'FOOD-010',
    name: 'Citrus Soda (1L)',
    description: 'Bebida cítrica artesanal, servida bien fría.',
      price: 6500,
    imageUrl:
      'https://images.unsplash.com/photo-1510626176961-4b57d4fbad03?auto=format&fit=crop&w=1200&q=80',
    category: 'Drinks',
    available: true,
  }),
];

export const foodDb = new Map(seedFoods.map((food) => [food.id, food]));

