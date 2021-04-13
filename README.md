## MARS Assembler
[MARS][1] is a lightweight interactive development environment (IDE) for programming in MIPS assembly language, intended for educational-level use

MARS has been jointly developed by [Pete Sanderson][4] (programming) and [Ken Vollmar][5] (details and paperwork).

This is an independent update maintained by [Valerio Colella][9]

## Download
You can download the latest version [in the releases section][6].

## Documentation (included in the repo)
 - Go to the [documentation][7].
 - In order to run or compile MM MARS, **MARS requires Java SE 8 (or later) SDK installed on your computer**.

## MM (More Modern) version notes 
 - As the original source is updated, the documentation might get outdated. Please let me know, or open a pull request.

## How to run MARS
 - **Option A**: Desktop. Save the jar file on the desktop. Run MARS by double-clicking the icon.
 - **Option B**: DOS shell using jar file. Save the jar file in some folder. Open a DOS shell in that folder. Rename the jar file to "Mars.jar" for convenience. Run MARS with the DOS command  java -jar Mars.jar
 - **Option C**: DOS shell using Java classes. Save the jar file in some folder. Open a DOS shell in that folder. Rename the jar file to "Mars.jar" for convenience. Extract MARS files with the DOS command  jar -xf Mars.jar Run MARS with the DOS command  java Mars

## How to compile
 - **Windows**: execute "CreateMarsJar.bat" file to generate an executable.
 - **GNU/Linux** and **Mac**: execute the "CreateMarsJar.sh" to generate an executable. If you can't due of permissions, do a "**chmod +x CreateMarsJar.sh**" (Thanks to @aesptux for testing the Mac version).

## License
[MIT][2]. Check the [LICENSE][3] file. Copyright held by the original developers.

  [1]: http://courses.missouristate.edu/KenVollmar/MARS/index.htm
  [2]: https://www.opensource.org/licenses/mit-license.html
  [3]: https://github.com/PrOF-kk/MM_MARS_Assembler/blob/master/LICENSE
  [4]: http://faculty.otterbein.edu/PSanderson/
  [5]: http://courses.missouristate.edu/KenVollmar/
  [6]: https://github.com/PrOF-kk/MM_MARS_Assembler/releases
  [7]: http://courses.missouristate.edu/KenVollmar/MARS/Help/MarsHelpIntro.html
  [9]: https://github.com/PrOF-kk