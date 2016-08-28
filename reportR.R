Sys.setlocale(, "zh_CN.UTF-8")

library(knitr)
library(markdown)

setwd("$dirR/MarkDown/reportR/RMD/$fileR")


knit("$fileR.Rmd")

markdownToHTML('$fileR.md', '$fileR.html')





