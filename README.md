# Wiki-Rank
Calculate PageRank values of Wikipedia pages based on reference relationships

## 数据集

### 地址

推荐数据集：https://dumps.wikimedia.org/backup-index.html

### 数据特征

所有wiki网页被压缩到一个xml文件中，从`<page>`tag开始，`</page>`tag结束。

页面中`[[]]`指向