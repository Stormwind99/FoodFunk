# FoodFunk

This is an automated Minecraft Forge mod template based on the Forge MDK.

Expected directory structure before run:
* newmod.sh (from Git repo other/util/newmod.sh)
* newmod (optional)
   * newmod.cfg (from Git repo other/util/newmod.cfg with options you've modified)
   * private.properties (optional)

Running ```newmod.sh NewMod``` will then create:

* NewModWorkspace
   * NewMod (local git repo for new mod with remote origin set)
      * (expected base Mod files)
      * Modify build.properties and private.properties
   * NewMod.wiki (local git repo for new mod's wiki with remote origin set)

## Notes

* To debug:
   * ```cp build/resources/main/META-INF/mods.toml src/main/resources/META-INF/mods.toml``` beforehand since debugger does not run build with gradle string replacement.  Then ```git checkout src/main/resources/META-INF/mods.toml``` afterward to revert back to generated mcmod.info template.
   * Also comment out Gradle string replacements in Reference.java and uncomment the explicit string constants below it (since debug won't do the Gradle string replacement)
