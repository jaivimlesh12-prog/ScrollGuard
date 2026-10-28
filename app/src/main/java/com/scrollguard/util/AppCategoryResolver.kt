package com.scrollguard.util

/**
 * Resolves a human-readable category for an Android app given its package name.
 *
 * Contains an internal map of 20+ well-known packages. Unknown packages
 * are categorised as [CATEGORY_OTHER].
 */
object AppCategoryResolver {

    const val CATEGORY_SOCIAL_MEDIA = "Social Media"
    const val CATEGORY_ENTERTAINMENT = "Entertainment"
    const val CATEGORY_GAMES = "Games"
    const val CATEGORY_PRODUCTIVITY = "Productivity"
    const val CATEGORY_COMMUNICATION = "Communication"
    const val CATEGORY_EDUCATION = "Education"
    const val CATEGORY_HEALTH = "Health"
    const val CATEGORY_NEWS = "News"
    const val CATEGORY_SHOPPING = "Shopping"
    const val CATEGORY_TOOLS = "Tools"
    const val CATEGORY_OTHER = "Other"

    private val packageCategoryMap: Map<String, String> = mapOf(
        // Social Media
        "com.instagram.android" to CATEGORY_SOCIAL_MEDIA,
        "com.twitter.android" to CATEGORY_SOCIAL_MEDIA,
        "com.facebook.katana" to CATEGORY_SOCIAL_MEDIA,
        "com.zhiliaoapp.musically" to CATEGORY_SOCIAL_MEDIA,      // TikTok
        "com.snapchat.android" to CATEGORY_SOCIAL_MEDIA,
        "com.reddit.frontpage" to CATEGORY_SOCIAL_MEDIA,
        "com.linkedin.android" to CATEGORY_SOCIAL_MEDIA,
        "com.pinterest" to CATEGORY_SOCIAL_MEDIA,

        // Entertainment
        "com.google.android.youtube" to CATEGORY_ENTERTAINMENT,
        "com.netflix.mediaclient" to CATEGORY_ENTERTAINMENT,
        "com.spotify.music" to CATEGORY_ENTERTAINMENT,
        "in.startv.hotstar" to CATEGORY_ENTERTAINMENT,             // Disney+ Hotstar
        "com.amazon.avod.thirdpartyclient" to CATEGORY_ENTERTAINMENT, // Prime Video

        // Games
        "com.supercell.clashofclans" to CATEGORY_GAMES,
        "com.mojang.minecraftpe" to CATEGORY_GAMES,
        "com.kiloo.subwaysurf" to CATEGORY_GAMES,
        "com.activision.callofduty.shooter" to CATEGORY_GAMES,
        "com.pubg.imobile" to CATEGORY_GAMES,                     // BGMI

        // Productivity
        "com.google.android.apps.docs" to CATEGORY_PRODUCTIVITY,
        "com.microsoft.office.outlook" to CATEGORY_PRODUCTIVITY,
        "com.notion.id" to CATEGORY_PRODUCTIVITY,
        "com.todoist" to CATEGORY_PRODUCTIVITY,

        // Communication
        "com.whatsapp" to CATEGORY_COMMUNICATION,
        "org.telegram.messenger" to CATEGORY_COMMUNICATION,
        "com.discord" to CATEGORY_COMMUNICATION,
        "com.Slack" to CATEGORY_COMMUNICATION,
        "com.google.android.apps.messaging" to CATEGORY_COMMUNICATION,

        // Education
        "com.duolingo" to CATEGORY_EDUCATION,
        "com.byju" to CATEGORY_EDUCATION,
        "com.google.android.apps.classroom" to CATEGORY_EDUCATION,

        // Health
        "com.google.android.apps.fitness" to CATEGORY_HEALTH,
        "com.calm.android" to CATEGORY_HEALTH,
        "com.headspace.android" to CATEGORY_HEALTH,

        // News
        "com.google.android.apps.magazines" to CATEGORY_NEWS,      // Google News
        "flipboard.app" to CATEGORY_NEWS,
        "com.twitter.android.lite" to CATEGORY_NEWS,

        // Shopping
        "com.amazon.mShop.android.shopping" to CATEGORY_SHOPPING,
        "com.flipkart.android" to CATEGORY_SHOPPING,
        "in.amazon.mShop.android.shopping" to CATEGORY_SHOPPING,

        // Tools / Browsers
        "com.android.chrome" to CATEGORY_TOOLS,
        "org.mozilla.firefox" to CATEGORY_TOOLS,
        "com.brave.browser" to CATEGORY_TOOLS,
        "com.opera.browser" to CATEGORY_TOOLS,
        "com.microsoft.emmx" to CATEGORY_TOOLS
    )

    /**
     * Returns the category for the given [packageName].
     *
     * Falls back to [CATEGORY_OTHER] when the package is not in the
     * internal lookup table.
     */
    fun getCategory(packageName: String): String {
        return packageCategoryMap[packageName] ?: CATEGORY_OTHER
    }
}
