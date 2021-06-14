:: Usage - build_one.bat imageNoExtension
:: Takes imageNoExtension.svg and outputs correct resolution pngs
mkdir .\png\32
mkdir .\png\24
mkdir .\png\16

inkscape -w 16 -h 16 %1.svg -o .\png\16\%1.png
inkscape -w 24 -h 24 %1.svg -o .\png\24\%1.png
inkscape -w 32 -h 32 %1.svg -o .\png\32\%1.png