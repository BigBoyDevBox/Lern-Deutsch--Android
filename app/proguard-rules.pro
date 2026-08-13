# R8 rules for Wortkatze.
#
# House warning (AGENTS.md): "works in debug, breaks in release" is almost
# always a missing keep rule for a new reflection/serialization entry point —
# check here first.

# kotlinx.serialization — the vocabulary asset, the saved progress document and
# the navigation routes are all serialized, so keep the generated serializers
# for everything in our own package.
-keepclassmembers class ch.lkmc.wortkatze.** {
    *** Companion;
}
-keepclasseswithmembers class ch.lkmc.wortkatze.** {
    kotlinx.serialization.KSerializer serializer(...);
}
