;;; Sierra Script 1.0 - (do not remove this comment)
;**************************************************************
;***
;***	GAME.SH--
;***
;**************************************************************


(include pics.sh) (include views.sh) ;graphical defines
(include system.sh) (include sci2.sh) ;system and kernel functions
(include talkers.sh) (include verbs.sh) ;message defines
;
; Global stuff

(define	MAIN			0)
(define GAME_WINDOW		1)
(define	DODISP			2)
(define	GAME_ROOM		3)
(define SPEED_TEST		4)
(define GAME_CONTROLS	5)
(define GAME_INV		6)
(define GAME_EGO		7)
(define DEBUG			8)
(define GAME_ABOUT		9)
(define DEATH			10)
(define GAME_ICONBAR	11)
(define GAME_INIT		12)
(define WHERE_TO		13)
(define DISPOSE_CODE	14)

;
; Actual rooms
(define	TITLE			100)
(define ROOM101			101)
(define	TESTROOM		110)

; Indices for the icons in the icon bar
(enum
	ICON_WALK
	ICON_LOOK
	ICON_DO
	ICON_TALK
	ICON_CUSTOM
	ICON_CURITEM
	ICON_INVENTORY
	ICON_CONTROL
	ICON_HELP
)

;Inventory items
(enum
	iMoney
	iLastInvItem	;this MUST be last
)
;(define NUM_INVITEMS (- iLastInvItem 1))

;Sound defines
(define sDeath 10)
(define sPoints 950)

;Death reasons
(enum 1
	deathGENERIC
)

;Flag handler defines
;NOTE: These are intended to replace the Bset, Btst, and Bclr procedures.
;However, SCICompanion does not yet support macro defines.
;;;(define Bset	gameFlags set:)
;;;(define Btst	gameFlags test:)
;;;(define Bclr	gameFlags clear:)
(define NUMFLAGS 128)

;Event flags
(enum
	fInMainGame
	fAutoSaveOn
	fWonGame
)