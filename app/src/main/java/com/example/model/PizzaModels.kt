package com.example.model

import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.Color

enum class AppLanguage {
    AR,
    EN,
}

enum class PizzaCategory(val labelAr: String, val labelEn: String) {
    ALL("الكل", "All"),
    CLASSIC("كلاسيك", "Classic"),
    MEAT("لحوم", "Meats"),
    VEGGIE("نباتي", "Veggie"),
    CHEESE("أجبان", "Cheeses"),
    SIDES("مقبلات", "Sides"),
}

enum class PizzaSize(
    val labelAr: String,
    val labelEn: String,
    val diameterCm: Int,
    val priceMultiplier: Double,
    val calorieFactor: Double,
) {
    SMALL("صغير", "Small", 25, 0.85, 0.78),
    MEDIUM("وسط", "Medium", 32, 1.0, 1.0),
    FAMILY("عائلي", "Family", 40, 1.35, 1.45),
}

enum class CrustType(
    val labelAr: String,
    val labelEn: String,
    val descAr: String,
    val descEn: String,
    val extraPriceEgp: Int,
) {
    NEAPOLITAN(
        labelAr = "نابولي منتفخ",
        labelEn = "Neapolitan Puff",
        descAr = "أطراف مقرمشة ومهواة بتخمير ٤٨ ساعة",
        descEn = "Airy blistered cornicione, 48h ferment",
        extraPriceEgp = 0,
    ),
    THIN_ROMAN(
        labelAr = "روماني رفيع",
        labelEn = "Thin Romana",
        descAr = "عجينة رقيقة ومقرمشة من الحافة للحافة",
        descEn = "Ultra-crisp thin artisan base",
        extraPriceEgp = 0,
    ),
    CHEESE_STUFFED(
        labelAr = "أطراف محشوة جبنة",
        labelEn = "Cheese-Stuffed Crust",
        descAr = "حواف ذهبية محشوة بموتزاريلا وشيدر",
        descEn = "Golden rim stuffed with mozzarella & cheddar",
        extraPriceEgp = 35,
    ),
}

enum class SauceType(
    val labelAr: String,
    val labelEn: String,
    val color: Color,
    val extraPriceEgp: Int,
) {
    SAN_MARZANO("صلصة سان مارزانو", "San Marzano Tomato", Color(0xFFC93424), 0),
    ALFREDO_WHITE("صوص ألفريدو بالثوم", "Garlic Alfredo", Color(0xFFF3E2C0), 15),
    SMOKY_BBQ("صوص باربكيو مدخن", "Smoky BBQ", Color(0xFF7B2E1E), 10),
    PESTO_BASIL("بيستو الريحان الطازج", "Fresh Basil Pesto", Color(0xFF3E7A38), 20),
}

enum class ToppingVisualType {
    PEPPERONI,
    MUSHROOM,
    OLIVES,
    BASIL,
    BELL_PEPPER,
    RED_ONION,
    JALAPENO,
    EXTRA_CHEDDAR,
    CHERRY_TOMATO,
}

data class ToppingItem(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val priceEgp: Int,
    val calories: Int,
    val visualType: ToppingVisualType,
    val isVegetarian: Boolean = true,
)

data class PizzaItem(
    val id: String,
    val nameAr: String,
    val nameEn: String,
    val shortDescAr: String,
    val shortDescEn: String,
    val chefStoryAr: String,
    val chefStoryEn: String,
    val category: PizzaCategory,
    val basePriceEgp: Int,
    val calories: Int,
    val prepMinutes: Int,
    val rating: Double,
    val reviewCount: Int,
    val bakeTempCelsius: Int,
    @DrawableRes val imageRes: Int,
    val ingredientsAr: List<String>,
    val ingredientsEn: List<String>,
    val isPopular: Boolean = false,
    val isSpicy: Boolean = false,
    val isVegetarian: Boolean = false,
)

data class CartItem(
    val cartKey: String,
    val titleAr: String,
    val titleEn: String,
    val subtitleAr: String,
    val subtitleEn: String,
    val unitPriceEgp: Int,
    val quantity: Int,
    @DrawableRes val imageRes: Int,
    val isCustom: Boolean = false,
)

data class PizzaStyleGuide(
    val id: String,
    val titleAr: String,
    val titleEn: String,
    val originAr: String,
    val originEn: String,
    val tempAr: String,
    val tempEn: String,
    val hydrationAr: String,
    val hydrationEn: String,
    val descriptionAr: String,
    val descriptionEn: String,
    @DrawableRes val imageRes: Int,
)

enum class OrderStage(
    val stepIndex: Int,
    val titleAr: String,
    val titleEn: String,
    val descAr: String,
    val descEn: String,
) {
    RECEIVED(
        0,
        "تم استلام طلبك",
        "Order Confirmed",
        "وصل طلبك للمطبخ الإيطالي وجارٍ تجهيز العجينة",
        "Kitchen received your order and prepped the station",
    ),
    DOUGH_STRETCHING(
        1,
        "فرد العجينة وإضافة الصلصة",
        "Hand-Stretching Dough",
        "الشيف يفرد العجينة يدوياً ويضيف المكونات الطازجة",
        "Hand-stretching 48h fermented dough with fresh toppings",
    ),
    WOOD_OVEN(
        2,
        "في فرن الحطب (485°C)",
        "Wood-Fired Oven (485°C)",
        "البيتزا تُخبز الآن على الحجر البركاني لمدة 90 ثانية",
        "Baking on volcanic stone for 90 seconds",
    ),
    OUT_FOR_DELIVERY(
        3,
        "في الطريق إليك ساخنة",
        "Out for Delivery",
        "بيتزتك في صندوق حراري في طريقها إلى باب منزلك",
        "On the way in an insulated thermal box",
    ),
    DELIVERED(
        4,
        "بالهناء والشفاء!",
        "Enjoy Your Pizza!",
        "تم تسليم الطلب بنجاح. نتمنى لك وجبة شهية!",
        "Delivered fresh and hot. Buon appetito!",
    ),
}

data class ActiveOrder(
    val orderNumber: String,
    val totalEgp: Int,
    val itemCount: Int,
    val isDelivery: Boolean,
    val addressOrNote: String,
    val stage: OrderStage,
    val placedTimeLabel: String,
)
