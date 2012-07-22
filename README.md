Google Music Cache Tagger
==================================

![Screenshot] (https://github.com/burntcookie90/GMusic_Offliner/raw/master/screenshot.png)

This is a small java program that will tag your offline music.

It assumes that you've already pulled your cache from your phone.

If you don't have external store on your phone:
	`adb pull /data/data/com.google.android.music/ .`
	
If you do have external storage on your phone:
	`adb pull /sdcard/data/com.google.android.music/ .`

Both of these commands will pull the cache into your current directory, change the `.` to the directory of your choice if you want.

Once you loads the .jar, you'll see three buttons.

The first button requires you to choose your `music.db` file, which can be found in the databases folder in the root of your **pulled** cache directory.

The second button requires you to chose the directory your music files are in. This is found under caches -> `music`. 

Then just hit tag, and it should do all the work for you!



You can find the download in the `Downloads` tab!
==================================================
