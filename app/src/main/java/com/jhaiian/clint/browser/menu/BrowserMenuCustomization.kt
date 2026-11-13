package com.jhaiian.clint.browser.menu

import android.content.SharedPreferences
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ChromeReaderMode
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.automirrored.filled.AddToHomeScreen
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.graphics.vector.ImageVector
import com.jhaiian.clint.R

enum class MenuSection { TABS, PAGE, TOOLS, PRIVACY }

enum class CustomizableMenuItem(val id: String, val section: MenuSection) {
    NEW_TAB("new_tab", MenuSection.TABS),
    NEW_INCOGNITO_TAB("new_incognito_tab", MenuSection.TABS),
    NEW_PROFILE_TAB("new_profile_tab", MenuSection.TABS),
    MUTE_TAB("mute_tab", MenuSection.TABS),
    SHARE("share", MenuSection.PAGE),
    OPEN_IN_APP("open_in_app", MenuSection.PAGE),
    CREATE_SHORTCUT("create_shortcut", MenuSection.PAGE),
    BOOKMARKS("bookmarks", MenuSection.PAGE),
    HISTORY("history", MenuSection.PAGE),
    READER_MODE("reader_mode", MenuSection.PAGE),
    TRANSLATE("translate", MenuSection.PAGE),
    DESKTOP_MODE("desktop_mode", MenuSection.PAGE),
    DOWNLOADS("downloads", MenuSection.TOOLS),
    MEDIA_CAPTURE("media_capture", MenuSection.TOOLS),
    USER_SCRIPTS("userscripts", MenuSection.TOOLS),
    QUIVER_GUARD("quiver_guard", MenuSection.PRIVACY),
    WEBSITE_BLOCKER("website_blocker", MenuSection.PRIVACY),
    DATA_SAVER("data_saver", MenuSection.PRIVACY),
    COOKIES_CONTROL("cookies_control", MenuSection.PRIVACY);

    companion object {
        val DEFAULT_ORDER: List<CustomizableMenuItem> = entries.toList()
        fun fromId(id: String): CustomizableMenuItem? = entries.find { it.id == id }
    }
}

fun CustomizableMenuItem.icon(): ImageVector = when (this) {
    CustomizableMenuItem.NEW_TAB -> Icons.Filled.Add
    CustomizableMenuItem.NEW_INCOGNITO_TAB -> Icons.Filled.VisibilityOff
    CustomizableMenuItem.NEW_PROFILE_TAB -> Icons.Filled.AccountCircle
    CustomizableMenuItem.SHARE -> Icons.Filled.Share
    CustomizableMenuItem.OPEN_IN_APP -> Icons.AutoMirrored.Filled.OpenInNew
    CustomizableMenuItem.CREATE_SHORTCUT -> Icons.AutoMirrored.Filled.AddToHomeScreen
    CustomizableMenuItem.DOWNLOADS -> Icons.Filled.Download
    CustomizableMenuItem.MEDIA_CAPTURE -> Icons.Filled.VideoLibrary
    CustomizableMenuItem.USER_SCRIPTS -> Icons.Filled.Code
    CustomizableMenuItem.QUIVER_GUARD -> Icons.Filled.Security
    CustomizableMenuItem.WEBSITE_BLOCKER -> Icons.Filled.Shield
    CustomizableMenuItem.BOOKMARKS -> Icons.Filled.BookmarkBorder
    CustomizableMenuItem.HISTORY -> Icons.Filled.History
    CustomizableMenuItem.READER_MODE -> Icons.AutoMirrored.Filled.ChromeReaderMode
    CustomizableMenuItem.TRANSLATE -> Icons.Filled.Translate
    CustomizableMenuItem.DESKTOP_MODE -> Icons.Filled.DesktopWindows
    CustomizableMenuItem.DATA_SAVER -> Icons.Filled.DataSaverOn
    CustomizableMenuItem.MUTE_TAB -> Icons.AutoMirrored.Filled.VolumeOff
    CustomizableMenuItem.COOKIES_CONTROL -> CookieIcon
}

fun CustomizableMenuItem.titleRes(): Int = when (this) {
    CustomizableMenuItem.NEW_TAB -> R.string.new_tab
    CustomizableMenuItem.NEW_INCOGNITO_TAB -> R.string.new_incognito_tab
    CustomizableMenuItem.NEW_PROFILE_TAB -> R.string.profiles_new_tab_in
    CustomizableMenuItem.SHARE -> R.string.share_url
    CustomizableMenuItem.OPEN_IN_APP -> R.string.menu_open_in_app
    CustomizableMenuItem.CREATE_SHORTCUT -> R.string.menu_create_shortcut
    CustomizableMenuItem.DOWNLOADS -> R.string.menu_downloads
    CustomizableMenuItem.MEDIA_CAPTURE -> R.string.menu_media_capture
    CustomizableMenuItem.USER_SCRIPTS -> R.string.user_scripts_title
    CustomizableMenuItem.QUIVER_GUARD -> R.string.menu_quiver_guard
    CustomizableMenuItem.WEBSITE_BLOCKER -> R.string.menu_website_blocker
    CustomizableMenuItem.BOOKMARKS -> R.string.menu_bookmarks
    CustomizableMenuItem.HISTORY -> R.string.menu_history
    CustomizableMenuItem.READER_MODE -> R.string.reader_mode
    CustomizableMenuItem.TRANSLATE -> R.string.translation_menu_translate
    CustomizableMenuItem.DESKTOP_MODE -> R.string.desktop_mode
    CustomizableMenuItem.DATA_SAVER -> R.string.menu_data_saver
    CustomizableMenuItem.MUTE_TAB -> R.string.menu_mute_tab
    CustomizableMenuItem.COOKIES_CONTROL -> R.string.menu_cookies_control
}

fun MenuSection.titleRes(): Int = when (this) {
    MenuSection.TABS -> R.string.menu_section_tabs
    MenuSection.PAGE -> R.string.menu_section_page
    MenuSection.TOOLS -> R.string.menu_section_tools
    MenuSection.PRIVACY -> R.string.menu_section_privacy
}

object BrowserMenuCustomizationStore {
    private const val PREF_ORDER = "browser_menu_item_order"
    private const val PREF_HIDDEN = "browser_menu_hidden_items"

    fun readOrder(prefs: SharedPreferences): List<CustomizableMenuItem> {
        val storedIds = prefs.getString(PREF_ORDER, null)?.split(",")?.filter { it.isNotBlank() } ?: emptyList()
        val storedItems = storedIds.mapNotNull { CustomizableMenuItem.fromId(it) }
        val missing = CustomizableMenuItem.DEFAULT_ORDER.filter { it !in storedItems }
        return storedItems + missing
    }

    fun readHidden(prefs: SharedPreferences): Set<CustomizableMenuItem> {
        val storedIds = prefs.getStringSet(PREF_HIDDEN, null) ?: emptySet()
        return storedIds.mapNotNull { CustomizableMenuItem.fromId(it) }.toSet()
    }

    fun writeOrder(prefs: SharedPreferences, order: List<CustomizableMenuItem>) {
        prefs.edit().putString(PREF_ORDER, order.joinToString(",") { it.id }).apply()
    }

    fun writeHidden(prefs: SharedPreferences, hidden: Set<CustomizableMenuItem>) {
        prefs.edit().putStringSet(PREF_HIDDEN, hidden.map { it.id }.toSet()).apply()
    }

    fun resetToDefault(prefs: SharedPreferences) {
        prefs.edit().remove(PREF_ORDER).remove(PREF_HIDDEN).apply()
    }

    fun visibleOrderedItems(prefs: SharedPreferences): List<CustomizableMenuItem> {
        val hidden = readHidden(prefs)
        return readOrder(prefs).filter { it !in hidden }
    }
}
