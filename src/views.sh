;;; Sierra Script 1.0 - (do not remove this comment)\
;********************************************************************
;***
;***	VIEWS.SH
;***	  View defines
;***
;********************************************************************

(define	vEgo		0)
(define vBox		1)
(define vTestBlock	4)
(define vInvItems	900)
	(define lInvCursors	1)

(define vWalkCursor 980)
(define vLookCursor 981)
(define vDoCursor	982)
(define vTalkCursor 983)
(define vHelpCursor 989)
(define vIconBar	990)
	(enum
		lWalkIcon
		lLookIcon
		lDoIcon
		lTalkIcon
		lItemIcon
		lInvIcon
		lExitIcon
		lControlIcon
		lDummyIcon
		lHelpIcon
		lCustomIcon
		lScoreIcon
		lDummyIcon2
		lDisabledIcon
	)
	
(define vInvIcons 991)
	(enum
		lInvHand
		lInvHelp
		lInvLook
		lInvOK
		lInvSelect
		lInvMore
	)

(define vControlIcons 995)
	(enum
		lSliderText
		lControlFixtures
		lSaveButton
		lRestoreButton
		lRestartButton
		lQuitButton
		lAboutButton
		lHelpButton
		lOKButton
		lModeButton
		lCurrentMode
	)
	
(define vDeathSkull 2000)