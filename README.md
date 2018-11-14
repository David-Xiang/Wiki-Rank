# Wiki-Rank

Calculate PageRank values of Wikipedia pages based on reference relationships.

### 数据集下载

推荐数据集：https://dumps.wikimedia.org/backup-index.html

### 计算过程总览

采用Java来处理整个Wikipedia PagetRank的计算，因为Java既能够保证较高的性能表现，又有着很强的高层抽象支持。整个处理过程分为三部分：

1. `parseXML()`：处理原始的`.xml`dump文件，利用正则表达式从中抽取出一定数量（100万）的条目和条目其中的引用。由于文件巨大，把处理结果分成1000份，储存在了1000个`.json`文件中。
2. `buildNodes()`：在这些条目的引用中去除不在这些条目中的引用。换言之，是让这些数据的相互引用成为一个封闭的集合。然后便可以据此来构建一个稀疏图。
3. `iterate()`：在这个稀疏图上实现PageRank算法，需要通过多次迭代直到数值收敛得到结果，因此在迭代过程中需要预设一个delta值下限，当所有此词条的pagerank值变化之和小于此下限时，输出结果。

下面来具体阐述每个过程的中的一些关键点。

### 解析XML文件

`Parser.java`负责解析原始的`.xml`dump文件。注意到`.xml`文件中以`<page>`和`</page>`分割每个wiki页面，有的页面为词条页面，有的不是。而词条页面具有`<title>`标签来容纳词条的标题，其下正文中以`[[]]`标签来容纳引用词条，需要注意的是`[[]]`中`|`最左边的才是引用词条的具体名称。

因此整个解析过程的处理方式就很明确了：整个过程都采用正则表达式来处理，首先寻找页面的分界线；在每个页面中首先寻找确认是否存在`<title>`tag，如果没有则跳过页面；如果有，则搜寻出所有的引用标签。

另外，还有一些需要特别判定的点。在wiki中类似于`Wiki:AWB`，`File:xxx.jpg`，`Category:xxx`这类的页面实际上是不算概念的；还有一类重定向词条，这类词条在用户访问时会自动跳转到另外的词条。在具体抽取词条时，对上述几类词条都做了过滤。

最后，采用JSON作为第一阶段的数据保存形式。JSON是一种轻量级的数据交换格式。它基于ECMAScript的一个子集，采用完全独立于编程语言的文本格式来存储和表示数据。由于Java原生不支持JSON的直接读写，因此这一阶段需要用到Google开发的`Gson`库。可以使用最新的`gson-2.8.5.jar`作为第三方库，加入到IDE中。

### 构建稀疏图

`NodesBuilder.java`负责构建PageRank算法需要用到的稀疏图，首先需要将不存在于标题集合中的那些引用删去。Node维护`id2title`和`title2id`的map，通过Hash运算来保证查找的高效性。稀疏图的基本单元是代表每一个词条的`Node`，具有`id`/`title`/`incident`/`exit`等属性，`incident`为引用当前词条的词条id列表，`exit`为当前词条引用的词条id列表，这样可以提高存储的效率。避免矩阵存储和矩阵运算。

### PageRank迭代

`PRIterator.java`负责实现PageRank算法。每一轮迭代，算法都会根据公式更新每个`Node`的`prValue`，直到所有的`prValue`收敛，则算法结束。

迭代`prValue`的公式如下：

$prValue(i) = \alpha \sum_{p_j \in M_{p_i}}{\frac{prValue(p_j)}{L(p_j)} + \frac{1-\alpha}{N}}$

简单说明，`prValue(i)`应该等于对于所有引用i的词条的`prValue`除以其引用的词条数求和。由于可能出现排序泄漏导致`prValue`不收敛，因此引入了一个random walk的过程，表示为$\frac{1-\alpha}{N}$，其中$\alpha$为一个参数，通常取值为0.85。

在进行迭代之前，还有一个特殊的问题需要处理。有的词条可能没有出边，同样导致`prValue`不收敛，可以想象`prValue`都会集中到这个词条上来。因此，这种情况，需要将这种词条改为引用所有词条。最后有效的解决了`prValue`不收敛的问题。

先预设一个最小的变化阈值`minDelta`，每一次迭代之后加总所有的词条`prValue`的变化，若小于`minDelta`或者迭代次数超过预设值，则停止迭代。

最后将计算结果按照`prValue`降序排列，输出结果。

### 结果分析

整个英文维基百科(en.wikipedia.org)号称有500+万词条，我选取了其中100万个词条开展实验。最后得到的结果前1000条保存在了`output.txt`中，格式如下：

```
United States	0.0034238696675064064
United States Census Bureau	0.0020078791359565353
The New York Times	0.0017253405762467458
World War II	0.0015418061186737656
United Kingdom	0.0013600435917293344
New York City	0.0012545708229670916
Germany	0.0011006874178189471
London	9.631456170962362E-4
Canada	9.553229556789992E-4
England	9.202739115570799E-4
Japan	8.610624363073337E-4
India	8.399677083566624E-4
Australia	8.12902296264488E-4
Italy	7.766229245469581E-4
2010 United States Census	7.594866938002611E-4
World War I	7.578315042297559E-4
California	7.572253883548071E-4
List of sovereign states	6.738680561767342E-4
Washington, D.C.	6.644179264564485E-4
Soviet Union	6.491304173356965E-4
Paris	6.443493531714571E-4
Democratic Party (United States)	6.368650948489718E-4
Netherlands	6.277215349186594E-4
Spain	6.214556640577618E-4
China	6.180347918419679E-4
Republican Party (United States)	6.169263934045203E-4
Russia	6.135552595765141E-4
Latin	6.119091803808114E-4
Marriage	5.902097197688562E-4
Europe	5.332217627795297E-4
Geographic Names Information System	5.323582023933755E-4
French language	5.260512650249649E-4
Scotland	5.232668476604735E-4
Sweden	4.971911028899033E-4
Oxford University Press	4.8672447992325154E-4
```

由于所有词条的`prValue`加总等于1，因此每个`prValue`都很小，最大的不过10^-3^量级。

从最前的数据可以看出大部分都是国家名称，也是反映了维基百科作为一个世界级百科全书的权威性和全球性，其中美国的很重要地区也有上榜。这些国家地区确实容易和很多词条发生关联。

另一大重要的词条是信息源，例如`United States Census Bureau`普查数据，`The New York Times`新闻数据，`Geographic Names Information System`地理数据，`Oxford University Press`语料数据。除此之外还有两次世界大战上榜，说明这两次人类历史上重要的历史事件与很多词条产生了关联。