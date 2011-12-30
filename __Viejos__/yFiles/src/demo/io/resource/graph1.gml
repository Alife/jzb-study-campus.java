Creator "Y"
Version 1.0
graph
[
	label	""
	directed	1
	node
	[
		id	0
		label	"Ellipse"
		graphics
		[
			x	0.0
			y	0.0
			w	50.0
			h	30.0
			type	"ellipse"
			fill	"#FFFFCC"
		]
	]
	node
	[
		id	1
		label	"Image"
		graphics
		[
			x	0.0
			y	300.0
			image	"wiese.gif"
			width	1.00000
		]
    LabelGraphics
    [
      anchor	"s"
    ]		
	]
	node
	[
		id	 2
		label	"rectangle"
		graphics
		[
			x	150.0
			y	150.0
			w	120.0
			h	30.0
			type	"rectangle"
		]
	]
	edge
	[
		source	0
		target	1
		label	"edge label"
		graphics
		[
			width	1
			type	"line"
			fill	"#000000"
			arrow	"last"
		]
	]
	edge
	[
		source	1
		target	2
		label	"another edge"
		graphics
		[
			width	1
			type	"line"
			fill	"#000000"
			arrow	"last"
		]
	]
	edge
	[
		source	2
		target	0
		label	"great edge!"
		graphics
		[
			width	1
			type	"line"
			fill	"#000000"
			arrow	"last"
		]
	]
]
