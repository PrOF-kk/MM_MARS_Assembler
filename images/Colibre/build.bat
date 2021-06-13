mkdir .\png\32
mkdir .\png\24
mkdir .\png\16

forfiles /m *.svg /c "cmd /c inkscape -w 16 -h 16 @file -o .\png\16\\@fname.png"
forfiles /m *.svg /c "cmd /c inkscape -w 24 -h 24 @file -o .\png\24\\@fname.png"
forfiles /m *.svg /c "cmd /c inkscape -w 32 -h 32 @file -o .\png\32\\@fname.png"