package com.pakbzer.model;

/**
 * Product categories / sections of the PakBzer store.
 * Each constant carries a human friendly display name and a hero image used
 * on the home page section cards.
 */
public enum Category {
    MEN("Men", "Men's Shalwar Kameez, Kurtas & Waistcoats",
            "https://images.unsplash.com/photo-1701365676249-9d7ab5022dec?w=800&h=400&fit=crop&q=80"),
    WOMEN("Women", "Lawn Suits, Sarees & Embroidered Dresses",
            "https://images.unsplash.com/photo-1610030469983-98e550d6193c?w=800&h=400&fit=crop&q=80"),
    KIDS("Kids", "Comfortable & Colourful Outfits for Kids",
            "https://images.unsplash.com/photo-1518831959646-742c3a14ebf7?w=800&h=400&fit=crop&q=80"),
    BOY("Boy", "Stylish Kurtas & Casuals for Boys",
            "https://images.unsplash.com/photo-1622290291468-a28f7a7dc6a8?w=800&h=400&fit=crop&q=80"),
    ADULT("Adult", "Premium Formal & Festive Wear for Adults",
            "https://images.unsplash.com/photo-1594938298603-c8148c4dae35?w=800&h=400&fit=crop&q=80");

    private final String displayName;
    private final String description;
    private final String heroImage;

    Category(String displayName, String description, String heroImage) {
        this.displayName = displayName;
        this.description = description;
        this.heroImage = heroImage;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getHeroImage() {
        return heroImage;
    }
}
