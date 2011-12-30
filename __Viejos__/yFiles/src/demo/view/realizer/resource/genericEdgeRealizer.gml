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
			x	103.0
			y	168.0
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
			x	261.0
			y	81.0
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
			x	364.0
			y	238.0
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
			x	236.0
			y	311.0
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
	edge
	[
		source	1
		target	2
		graphics
		[
			customconfiguration	"QuadCurve"
			userdataclass	"java.lang.String"
			userdata	"This is my own userData object."
			width	4
			fill	"#00FF00"
			Line
			[
				point
				[
					x	261.0
					y	81.0
				]
				point
				[
					x	362.0
					y	80.0
				]
				point
				[
					x	364.0
					y	238.0
				]
			]
		]
	]
	edge
	[
		source	2
		target	3
		graphics
		[
			customconfiguration	"PolyLineAxesParallel"
			userdataclass	"java.lang.String"
			userdata	"This is my own userData object."
			width	4
			fill	"#00FF00"
			Line
			[
				point
				[
					x	364.0
					y	238.0
				]
				point
				[
					x	365.0
					y	318.0
				]
				point
				[
					x	236.0
					y	311.0
				]
			]
		]
		edgeAnchor
		[
			xSource	0.06666667014360428
			ySource	0.46666666865348816
			yTarget	0.46666666865348816
		]
	]
	edge
	[
		source	3
		target	0
		graphics
		[
			customconfiguration	"UndulatingCustomBends"
			userdataclass	"java.lang.String"
			userdata	"This is my own userData object."
			width	4
			fill	"#00FF00"
			Line
			[
				point
				[
					x	236.0
					y	311.0
				]
				point
				[
					x	100.0
					y	310.0
				]
				point
				[
					x	103.0
					y	168.0
				]
			]
		]
	]
	edge
	[
		source	1
		target	0
		graphics
		[
			customconfiguration	"Undulating"
			userdataclass	"java.lang.String"
			userdata	"This is my own userData object."
			width	4
			fill	"#00FF00"
			Line
			[
				point
				[
					x	261.0
					y	81.0
				]
				point
				[
					x	98.0
					y	82.0
				]
				point
				[
					x	103.0
					y	168.0
				]
			]
		]
	]
]
