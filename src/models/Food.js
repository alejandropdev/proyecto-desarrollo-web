export class Food {
  constructor({ id, name, description, price, imageUrl, category, available }) {
    this.id = id;
    this.name = name;
    this.description = description;
    this.price = price;
    this.imageUrl = imageUrl;
    this.category = category;
    this.available = available;
  }

  toObject() {
    return {
      id: this.id,
      name: this.name,
      description: this.description,
      price: this.price,
      imageUrl: this.imageUrl,
      category: this.category,
      available: this.available,
    };
  }
}

