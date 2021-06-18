# -*- coding: utf-8 -*-

# Script to build the majority of an html ASCII table.
# Control character should be added by hand
import html
with open("./output.html", "w") as f:

    filecontent = ""

    for i in range(32):

        filecontent += "<tr>\n"

        for j in range(4):
            dec = i + (j*32)
            hex = format(dec, "x")
            char = chr(dec)

            filecontent += f"\t<td>{dec}</td>\n\t<td>{hex}</td>\n"

            char = html.escape(char) if char.isprintable() else "COMMAND"
            filecontent += f"\t<td>{char}</td>\n"

            if j != 3:
                filecontent += "\n"

        filecontent += "</tr>\n"

    f.write(filecontent)