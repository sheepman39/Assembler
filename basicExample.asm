LDS	#3	.Initialize Register S to 3
	LDT	#300	.Initialize Register T to 300
	LDX	#0	.Initialize Index Register to 0
ADDLP	LDA	ALPHA,X	.Load Word from ALPHA into Register A
	ADD	BETA,X	.Add Word From BETA
	STA	GAMMA,X	.Store the Result in a work in GAMMA
	ADDR	S,X	.ADD 3 to INDEX value
	COMPR	X,T	.Compare new INDEX value to 300
	JLT	ADDLP	.Loop if INDEX value is less than 300
ALPHA	RESW	100
BETA	RESW	100
GAMMA	RESW	100