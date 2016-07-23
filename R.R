Sys.setlocale(, "zh_CN.UTF-8")

library(knitr)
library(markdown)

setwd("/Users/datatalks/DT/Dev/play2slick3/MarkDown")

# par(family='STKaiti')

# knitr::opts_chunk$set(echo=FALSE, fig.path='public/figure/', cache=FALSE)

knit("preview1.Rmd")

markdownToHTML('preview1.md', 'preview1.html')

# markdownToHTML("preview8.md", "app/views/preview888888.scala.html")



