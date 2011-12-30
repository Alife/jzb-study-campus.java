Creator	"yFiles"
Version	"2.4"
graph
[
	label	""
	directed	1
	node
	[
		id	0
		label	"1"
		graphics
		[
			x	0.0
			y	15.0
			w	30.0
			h	30.0
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"1"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	1
		label	"2"
		graphics
		[
			x	102.5
			y	90.0
			w	30.0
			h	30.0
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"2"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	2
		label	"3"
		graphics
		[
			x	177.5
			y	155.0
			w	30.0
			h	30.0
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"3"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	3
		label	"4"
		graphics
		[
			x	320.0
			y	220.0
			w	30.0
			h	30.0
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"4"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	4
		label	"3"
		graphics
		[
			x	242.5
			y	155.0
			w	30.0
			h	30.0
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"3"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	node
	[
		id	5
		label	"3"
		graphics
		[
			x	242.5
			y	90.0
			w	30.0
			h	30.0
			type	"rectangle"
			fill	"#CCCCFF"
			outline	"#000000"
		]
		LabelGraphics
		[
			text	"3"
			fontSize	12
			fontName	"Dialog"
			anchor	"c"
		]
	]
	edge
	[
		source	0
		target	1
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	0.0
					y	15.0
				]
				point
				[
					x	-7.5
					y	55.0
				]
				point
				[
					x	102.5
					y	55.0
				]
				point
				[
					x	102.5
					y	90.0
				]
			]
		]
		edgeAnchor
		[
			xSource	-0.5
			ySource	1.0
			yTarget	-1.0
		]
	]
	edge
	[
		source	1
		target	2
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	102.5
					y	90.0
				]
				point
				[
					x	102.5
					y	120.0
				]
				point
				[
					x	177.5
					y	120.0
				]
				point
				[
					x	177.5
					y	155.0
				]
			]
		]
		edgeAnchor
		[
			ySource	1.0
			yTarget	-1.0
		]
	]
	edge
	[
		source	2
		target	3
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	177.5
					y	155.0
				]
				point
				[
					x	177.5
					y	185.0
				]
				point
				[
					x	320.0
					y	185.0
				]
				point
				[
					x	320.0
					y	220.0
				]
			]
		]
		edgeAnchor
		[
			ySource	1.0
			yTarget	-1.0
		]
	]
	edge
	[
		source	0
		target	5
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
			Line
			[
				point
				[
					x	0.0
					y	15.0
				]
				point
				[
					x	7.5
					y	45.0
				]
				point
				[
					x	242.5
					y	45.0
				]
				point
				[
					x	242.5
					y	90.0
				]
			]
		]
		edgeAnchor
		[
			xSource	0.5
			ySource	1.0
			yTarget	-1.0
		]
	]
	edge
	[
		source	5
		target	4
		graphics
		[
			fill	"#000000"
			targetArrow	"standard"
		]
		edgeAnchor
		[
			ySource	1.0
			yTarget	-1.0
		]
	]
]
