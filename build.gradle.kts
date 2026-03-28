
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Defines plugins available to sub-modules but does NOT apply them here.
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.parcelize) apply false // Make the parcelize plugin available
}
