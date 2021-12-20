;;; Sierra Script 1.0 - (do not remove this comment)
;;;;
;;;;	LOGGER.SC
;;;;
;;;;	(c) Sierra On-Line, Inc, 1993
;;;;
;;;;	Author: 	Unknown
;;;;	Updated:	Brian K. Hughes
;;;;
;;;;	The logger is used to record notes about bugs found during the QA and
;;;;	beta processes.
;;;;
;;;;	Procedures:
;;;;		Log
;;;;
;;;;	Instances:
;;;;		sysLogger


(script# LOGGER)
(include game.sh)
(use Main)
(use String)
(use Print)
(use PolyPath)
(use System)

;;;(procedure
;;;	Log
;;;)

(public
	sysLogger 0
)

(local
	logHandle = 0
)

(enum 1
	Txt
	Num
	Uns
	Hex
	Inp
	Tim
	Dat
)

(define MAXCOMMENTS 10)

(procedure (Log how aLabel anArg &tmp buffer tm retval)
	(= buffer (String newWith: 80 {}))
	(buffer format: {%15s: } aLabel)
	(FileIO FileFPuts logHandle (buffer data?))
	(buffer copy: {})

	(switch how
		(Txt	(buffer copy: (if anArg anArg else {})))
		(Num	(buffer format: {%d} anArg))
		(Uns	(buffer format: {%u} anArg))
		(Hex	(buffer format: {%x} anArg))
		(Inp
			(if anArg
				(GetInput buffer 50 anArg 999)
			)
			(= retval (buffer size:))
		)
		(Tim
			; get system time stamp (minutes after 12:00)
			(= tm (GetTime SysTime2))
			(buffer format: 
				{%02d:%02d:%02d}
				(>> tm 11)
				(& (>> tm 5) %111111)
				(* (& tm %11111) 2)
			)
		)
		(Dat
			; get system date stamp (mm/dd/yy)
			(= tm (GetTime SysDate))
			(buffer format:
				{%02d/%02d/%02d}
				(& (>> tm 5) %1111)
				(& tm %11111)
				(+ 80 (>> tm 9))
			)
		)
	)
	
	(buffer cat: {\r})
	(FileIO FileFPuts logHandle (buffer data?))
	(buffer dispose:)
	retval
);procedure Log


(instance sysLogger of Code
	
	(method (doit 
			&tmp	i j l c firstNote theDrv commented saveInfont
			str
			cfgPath
			thePath
			theToken
			QAinitials
			kbdDrvEntry
			joyDrvEntry
			videoDrvEntry
			soundDrvEntry
			mouseDrvEntry
			audioDrvEntry
		)

		;;	initialize some variables:

		(= str 				(String new: 80))
		(= cfgPath 			(String new: 60))
		(= thePath 			(String new: 60))
		(= theToken  		(String new: 60))
		(= QAinitials 		(String new: 40))
		(= kbdDrvEntry 	(String new: 80))
		(= joyDrvEntry 	(String new: 80))
		(= videoDrvEntry 	(String new: 80))
		(= soundDrvEntry 	(String new: 80))
		(= mouseDrvEntry 	(String new: 80))
		(= audioDrvEntry 	(String new: 80))
		
		(= saveInfont inputFont)
		(= inputFont 999)
		
		(= firstNote (not (sysLogPath size:)))
		
		;; if path argument is NULL, we need some initial info 
		(if firstNote
			(while (not (< 0 (thePath size:) 19))
				(Print
				 	font:		999,
					fore:		0,
					back:		(Palette PalMatch 127 127 127),
				 	addText:	{Enter drive letter, path, and your name\n
				 				(no extension, max 40 characters)},
				 	addEdit: thePath 40 0 30,
				 	init:
				)
			)
			(sysLogPath copy: (thePath data?))
		)

;		; This commented code was intended to check disk space before
;		;	trying to write a log, but the kernel call FileIO FileCheckFreeSpace
;		;	doesn't appear to be working!
;
;		; First thing we do is check available disk space
;		; If we don't have at least 2k, don't even bother
;		;	with the rest of this - BKH
;
;		(if (not (FileIO FileCheckFreeSpace (sysLogPath data?)))
;			(Print
;				font:			999,
;				addText:		{Error: Not enough disk space for new entry},
;				init:
;			)
;			(return FALSE)
;		)

		;;access "memory variable" file to seed data
		(thePath format: {%s.mem} (KArray ArrayGetData sysLogPath))
		
		(if (!= -1 (= logHandle (FileIO FileOpen (thePath data?) fRead)))
			(FileIO FileFGets (QAinitials data?) 80 logHandle)
			(FileIO FileFGets (cfgPath data?) 80 logHandle)
			(FileIO FileClose logHandle)
		else
			(QAinitials copy: {})
			(cfgPath copy: {resource.cfg})
		)
		
		(if firstNote
			(Print
				font:		999,
				addText:	{Enter your login name\n
							(max 8 characters):},
				addEdit: QAinitials 12 0 30,
				init:
			)
			(QAinitials at: 8 NULL)
		)
		
		;; read configuration file
		(while
			(and
				(or
					(not firstNote)
					(Print
						font:		999,
						addText:	{Enter configuration file name:},
						addEdit:	cfgPath 30 0 30 cfgPath,
						init:
					)
			  	)
			  	(cfgPath at: 0)
			  	(== -1 (= logHandle (FileIO FileOpen (cfgPath data?) fRead)))
			)
			(cfgPath at: 0 0)
		)

		(if (!= -1 logHandle) ; opened config file
			(while (FileIO FileFGets (str data?) 80 logHandle)
				
				; strip leading whitespace
				(for 
					((= i 0)) 
					(and (= c (str at: i)) (OneOf c TAB SPACEBAR)) 
					((++ i))
				)
				
				(for 	((= j 0))
						(and	(= c (str at: i))
								(not (OneOf c `= `: TAB SPACEBAR))
						) 
						((++ i) (++ j))
					(theToken at: j c)
				)
				
				(theToken at: j NULL)
				
				(= theDrv
					(cond
						((theToken compToFrom: 0 {kbdDrv} 0 6)
							kbdDrvEntry
						)
						((theToken compToFrom: 0 {joyDrv} 0 6)
							joyDrvEntry
						)
						((theToken compToFrom: 0 {videoDrv} 0 8)
							videoDrvEntry
						)
						((theToken compToFrom: 0 {soundDrv} 0 8)
							soundDrvEntry
						)
						((theToken compToFrom: 0 {mouseDrv} 0 8)
							mouseDrvEntry
						)
						((theToken compToFrom: 0 {audioDrv} 0 8)
							audioDrvEntry
						)
						(else
							0
						)
					)
				)

				(if theDrv
					
					;;skip trailing white space
					(while (and (= c (str at: i)) (OneOf c `= `: TAB SPACEBAR))
						(++ i)
					)

					;;find last delimiter and period
					(for	((= j i) (= l 0))
							(= c (str at: j))
							((++ j))

						(cond
							((OneOf c `: `\;\
									`/)
								(= i (+ j 1))
							)
							((== c `.)
								(= l (- j i))
							)
						)
					)
					(if (== l 0)
						(= l (- j i))
					)
					
					(theDrv copyToFrom: 0 (str data?) i l)
				)
				
			)
			
			(FileIO FileClose logHandle)
			
		)
		
		;;NOW, open log file!
		(thePath format: {%s.log} (KArray ArrayGetData sysLogPath))
		(if (and
				firstNote
				(or
					;; file doesn't exist so start new one
					(== -1 (= logHandle (FileIO FileOpen (thePath data?) fRead)))
					;; file exists, ask whether to overwrite
					(and (str format: {Log file \"%s\" exists} (thePath data?)) FALSE)
					(Print
						font:				999,
						addText:			str,
						addButtonBM: 	SAVE 2 0 FALSE {Append to it} 0 12,
						addButtonBM: 	SAVE 2 0 TRUE	{Overwrite it} 75 12,
						init:
					)
				)
			)
			(FileIO FileClose logHandle)
			(= logHandle (FileIO FileOpen (thePath data?) fTrunc))
		else
			(= logHandle (FileIO FileOpen (thePath data?) fAppend))
		)
		
		(if (== -1 logHandle)
			(Print
				font:			999,
				addTextF:	{Error: Couldn't open %s} thePath,
				init:
			)
			
		else
			
			;;�������������������������������������ͻ
			;;�    Match Fields With Import Items   �
			;;�������������������������������������͹
			;;� Number of Items in Import File: 63  �
			;;�������������������������������������͹
			;;� Item � Field Name   � Type � Length �
			;;�������������������������������������͹
			;;�   1  � BUG-NUMBER   �   N  �     7  �
			;;�   2  � BACKWARD     �   B  �     7  �
			;;�   3  � FORWARD      �   F  �     7  �
			;;�   4  � GAME         �   A  �     6  �
			
			(Log Txt {GAME} (theGame name?))
			
			;;�   5  � VERSION      �   A  �     7  �
			
			(Log Txt {VERSION} (if version version else {unknown}))
			
			;;�   6  � QA-DATE      �   D  �     6  �
			
			(Log Dat {QA-DATE})
			
			;;�   7  � ANALYST      �   A  �     3  �
			
			(Log Txt {ANALYST} (QAinitials data?))
			
			;;�   8  � QA-STATUS    �   A  �     1  �
			;;�   9  � RE-CHECK     �   D  �     6  �
			;;�  10  � SEVERITY     �   A  �     1  �
			
			(Log Txt {SEVERITY}
				(Print
 					font:				999,
					addText:			{Severity of bug...},
					addButtonBM: 	SAVE 2 0 {Fatal} 		{FATAL}		0 12,
					addButtonBM: 	SAVE 2 0 {Moderate} 	{MODERATE} 	70 12,
					addButtonBM: 	SAVE 2 0 {Minor} 		{MINOR} 		140 12,
					saveCursor:		TRUE,
					init:
				)
			)
			
			;;�  11  � REPRODUCIBLE �   A  �     3  �

			(Log Txt {REPRODUCIBLE}
				(Print
					font:				999,
					addText:			{Reproducible?},
					addButtonBM:	SAVE 0 0	{Yes} 			{YES}			0 12,
					addButtonBM:	SAVE 0 0	{No} 				{NO}		  	55 12,
					addButtonBM:	SAVE 2 0	{Intermittent} {INTERMIT.}	110 12,
					saveCursor:		TRUE,
					init:
				)
			)

			;;�  12  � QA-COMMENT1  �   A  �    76  �
			;;�  13  � QA-COMMENT2  �   A  �    76  �
			;;�  14  � QA-COMMENT3  �   A  �    76  �
			;;�  15  � QA-COMMENT4  �   A  �    76  �
			;;�  16  � QA-COMMENT5  �   A  �    76  �
			;;�  17  � QA-COMMENT6  �   A  �    76  �
			;;�  18  � QA-COMMENT7  �   A  �    76  �
			;;�  19  � QA-COMMENT8  �   A  �    76  �
			;;�  20  � QA-COMMENT9  �   A  �    76  �
			;;�  21  � QA-COMMENT10 �   A  �    76  �
			
			(for 
				((= i 1) (= commented TRUE)) 
				(<= i MAXCOMMENTS)
				((++ i)) 
				
				(theToken format: {QA-COMMENT%d} i)
				(str format: {Comment line %d of %d:} i MAXCOMMENTS)
				(if commented
					(= commented (Log Inp (theToken data?) (str data?)))
				else
					(Log Txt (theToken data?) NULL)
				)
			)
			;;�  22  � DEPARTMENT   �   A  �     1  �
			
			(Log Txt {DEPARTMENT}
				(Print
					font:				999,
					addText:			{Who can fix bug...},
					addButtonBM: 	SAVE 0 0 {Art} 			{ART} 		0 12,
					addButtonBM: 	SAVE 0 0 {Programming} 	{PROG} 		55 12,
					addButtonBM:	SAVE 0 0 {Music}			{MUSIC}		110 12,
					addButtonBM: 	SAVE 0 0 {Design} 		{DESIGN} 	165 12,
					saveCursor:		TRUE,
					init:
				)
			)
			
			;;�  23  � RESPONSE-BY  �   A  �     3  �
			;;�  24  � RESP-DATE    �   D  �     6  �
			;;�  25  � ACTION       �   A  �     1  �
			;;�  26  � RESPONSE-1   �   A  �    76  �
			;;�  27  � RESPONSE-2   �   A  �    76  �
			;;�  28  � RESPONSE-3   �   A  �    76  �
			;;�  29  � RESPONSE-4   �   A  �    76  �
			;;�  30  � RESPONSE-5   �   A  �    76  �
			
			;;�  31  � ROOM         �   N  �     4  �
			
			(str format: {%5hu} curRoomNum) ;increased size to 5, also 
			(Log Txt {ROOM} (str data?))	  ; used unsigned short -gtp
			
			;;�  32  � ROOM-SCRIPT  �   A  �    15  �
			;;�  33  � ROOM-STATE   �   N  �     5  �
			
			(= i (curRoom script?))
			(Log Txt {ROOM-SCRIPT} (if i (i name?)))
			(Log Num {ROOM-STATE} (if i (i state?)))
			
			;;�  34  � EGO-X        �   A  �     3  �
			;;�  35  � EGO-Y        �   A  �     3  �
			;;�  36  � EGO-Z        �   A  �     3  �
			
			(Log Num {EGO-X} (ego x?))
			(Log Num {EGO-Y} (ego y?))
			(Log Num {EGO-Z} (ego z?))
			
			;;�  37  � EGO-SCRIPT   �   A  �    15  �
			;;�  38  � EGO-STATE    �   N  �     5  �
			
			(= i (ego script?))
			(Log Txt {EGO-SCRIPT} (if i (i name?)))
			(Log Num {EGO-STATE} (if i (i state?)))
			
			;;�  39  � EGO-VIEW     �   A  �     4  �
			;;�  40  � EGO-LOOP     �   A  �     2  �
			;;�  41  � EGO-CEL      �   A  �     2  �
			;;�  42  � EGO-PRIORITY �   A  �     3  �
			;;�  43  � EGO-HEADING  �   A  �     3  �
			
			(Log Num {EGO-VIEW} (ego view?))
			(Log Num {EGO-LOOP} (ego loop?))
			(Log Num {EGO-CEL} (ego cel?))
			(Log Num {EGO-PRIORITY} (ego priority?))
			(Log Num {EGO-HEADING} (ego heading?))
			
			;;�  44  � EGO-CYCLER   �   A  �    15  �
			
			(Log Txt {CYCLER} (if (ego cycler?) ((ego cycler?) name?)))
			
			;;�  45  � EGO-MOVER    �   A  �    15  �
			;;�  46  � MOVER-X      �   A  �     3  �
			;;�  47  � MOVER-Y      �   A  �     3  �
			;;�  48  � EGO-MOVESPD  �   A  �     4  �
			
			(= i (ego mover?))
			(Log Txt {EGO-MOVER} (if i (i name?)))
			(Log Num {MOVER-X} 
				(cond 
					((not i) NULL)
					((i isMemberOf: PolyPath) (i finalX?))
					(else (i x?))
				)
			)
			(Log Num {MOVER-Y} 
				(cond 
					((not i) NULL)
					((i isMemberOf: PolyPath) (i finalY?))
					(else (i y?))
				)
			)
			
			(Log Num {EGO-MOVESPD} (ego moveSpeed?))
			
			;;�  49  � SIGNAL-BITS  �   A  �     4  �
			
			(Log Hex {SIGNAL-BITS} (ego signal?))
			
			;;�  50  � ILLEGAL-BITS �   A  �     4  �
			
			(Log Hex {ILLEGAL-BITS} (ego illegalBits?))
			
			;;�  51  � HOWFAST      �   A  �     1  �
			
			(Log Num {HOWFAST} howFast)
			
			;;�  52  � ICONBAR      �   A  �    15  �
			
			(Log Txt {ICONBAR} (if theIconBar (theIconBar name?)))
			(Log Txt {CUR-ICON} 
				(if (and theIconBar (theIconBar curIcon?))
					((theIconBar curIcon?) name?)
				)
			)
			;;�  53  � DETAIL-LEVEL �   A  �     2  �
			
			(Log Num {DETAIL-LEVEL} (theGame detailLevel:))
			
			;;�  54  � CD-AUDIO     �   A  �     1  �
			
			(Log Num {CD-AUDIO} (& msgType CD_MSG))
			
			;;�  55  � VIDEO-DRV    �   A  �     8  �
			;;�  56  � SOUND-DRV    �   A  �     8  �
			;;�  57  � AUDIO-DRV    �   A  �     8  �
			;;�  58  � KEYBOARD-DRV �   A  �     8  �
			;;�  59  � JOY-DRV      �   A  �     8  �
			;;�  60  � MOUSE        �   A  �     1  �
			
			(Log Txt {VIDEO-DRV} (videoDrvEntry data?))
			(Log Txt {SOUND-DRV} (soundDrvEntry data?))
			(Log Txt {AUDIO-DRV} (audioDrvEntry data?))
			(Log Txt {KEYBOARD-DRV} (kbdDrvEntry data?))
			(Log Txt {JOY-DRV} (joyDrvEntry data?))
			(Log Txt {MOUSE} (mouseDrvEntry data?))
			
			
			;;�  61  � LARGEST-HEAP �   A  �     5  �
			;;�  62  � FREE-HEAP    �   A  �     5  �
			;;�  63  � TOTAL-HUNK   �   A  �     3  �
			;;�  64  � LARGEST-HUNK �   A  �     5  �
			;;�  65  � FREE-HUNK    �   A  �     3  �
			
			(Log Uns {LARGEST-HEAP} (MemoryInfo MemLargest))
			(Log Uns {FREE-HEAP} (MemoryInfo MemLargest))
			(Log Uns {TOTAL-HUNK} (>> (MemoryInfo MemLargest) 6))
			(Log Uns {LARGEST-HUNK} (MemoryInfo MemLargest))
			(Log Uns {FREE-HUNK} (>> (MemoryInfo MemLargest) 6))
			
			;;�������������������������������������ͼ
			
			(FileIO FileFPuts logHandle {**********************************\r})
			(FileIO FileClose logHandle)
		)
		
		(thePath format: {%s.mem} (KArray ArrayGetData sysLogPath))
		(if (and
				(== -1 (= logHandle (FileIO FileOpen (thePath data?) fTrunc))) ;existing file
				(== -1 (= logHandle (FileIO FileOpen (thePath data?) fAppend))) ;new file
			)
			(Print
				font:			999,
				addTextF:	{Error: Couldn't open memory file %s!} (thePath data?),
				init:
			)
		else
			(FileIO FileFPuts logHandle (QAinitials data?))
			(FileIO FileFPuts logHandle {\n})
			(FileIO FileFPuts logHandle (cfgPath data?))
			(FileIO FileFPuts logHandle {\n})
			(FileIO FileClose logHandle)
		)
		
		(= inputFont saveInfont)
		
		(str dispose:)
		(cfgPath dispose:)
		(thePath dispose:)
		(theToken dispose:)
		(QAinitials dispose:)
		(kbdDrvEntry dispose:)
		(joyDrvEntry dispose:)
		(videoDrvEntry dispose:)
		(soundDrvEntry dispose:)
		(mouseDrvEntry dispose:)
		(audioDrvEntry dispose:)

		;; unload me
		(DisposeScript LOGGER)
	)
)
