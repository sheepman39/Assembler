COPY	START	0	.COPY FILE FROM INPUT TO OUTPUT
RDBUFF	MACRO	&INDEV,&BUFADR,&RECLTH	
.			
.	MACRO TO READ RECORD INTO BUFFER			
.			
	CLEAR	X	.CLEAR LOOP COUNTER
	CLEAR	A	
	CLEAR	S	
	+LDT	#4096	.SETMAXIMUM RECORD LENGTH
	TD	=X'&INDEV'	.TEST INPUT DEVICE
	JEQ	*-3	.LOOP UNTIL READY
	RD	=X'&INDEV'	.READ CHARACTER INTO REG A
	COMPR	A,S	.TEST FOR END OF RECORD
	JEQ	*+11	.EXIT LOOP IF EOR
	STCH	&BUFADR,X	.STORE CHARACTER IN BUFFER
	TIXR	T	.LOOP UNLESS MAXIMUM LENGTH HAS BEEN REACHED
	JLT	*-19	
	STX	&RECLTH	.SAVE RECORD LENGTH
	MEND		
WRBUFF	MACRO	&OUTDEV,&BUFADR,&RECLTH	
.			
.	MACRO TO WRITE RECORD FROM BUFFER			
.			
	CLEAR	X	.CLEAR LOOP COUNTER
	LDT	&RECLTH	
	LDCH	&BUFADR,X	.GET CHARACTER FROM BUFFER
	TD	=X'&OUTDEV'	.TEST OUTPUT DEVICE
	JEQ	*-3	.LOOP UNTIL READY
	WD	=X'&OUTDEV'	.WRITE CHARACTER
	TIXR	T	.LOOP UNTIL ALL CHARACTERS HAVE BEEN WRITTEN
	JLT	*-14	
	MEND		
.			
.	MAIN PROGRAM			
.			
FIRST	STL	RETADR	.SAVE RETURN ADDRESS
CLOOP	RDBUFF	F1,BUFFER,LENGTH	.READ RECORD INTO BUFFER
	LDA	LENGTH	.TEST FOR END OF FILE
	COMP	#0	
	JEQ	ENDFIL	.EXIT IF EOF FOUND
	WRBUFF	05,BUFFER,LENGTH	.WRITE OUTPUT RECORD
	J	CLOOP	.LOOP
ENDFIL	WRBUFF	05,BUFFER,THREE	.WRITE OUTPUT RECORD
	J	@RETADR	
EOF	BYTE	C'EOF'	
THREE	WORD	3	
RETADR	RESW	1	
LENGTH	RESW	1	.LENGTH OF RECORD
BUFFER	RESB	4096	.4096-BYTE BUFFER AREA
	END	FIRST