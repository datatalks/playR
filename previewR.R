Sys.setlocale(, "zh_CN.UTF-8")

library(knitr)
library(markdown)

setwd("/Users/datatalks/DT/Dev/playR/MarkDown/RMD")


knit("$fileR.Rmd")

markdownToHTML('$fileR.md', '$fileR.html')





