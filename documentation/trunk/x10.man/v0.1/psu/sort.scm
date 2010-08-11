(declare (usual-integrations))

(define (make-index #!optional input-filename output-filename)
  (write-index (if (default-object? output-filename)
		   "index.tex"
		   output-filename)
	       (read-index (if (default-object? input-filename)
			       "r4rs.idx"
			       input-filename))))

(define (read-index pathname)
  (map (lambda (entry)
	 (make-page-reference (massage-sort-key (list-ref entry 1))
			      (list-ref entry 2)
			      (list-ref entry 3)
			      (list-ref entry 4)))
       (with-current-parser-table parser-table/index
	 (lambda ()
	   (read-file pathname)))))

(define parse-string
  (let ((delimiters (char-set #\"))
	(environment (->environment '(RUNTIME PARSER))))
    (lambda ()
      (let ((port (access *parser-input-port* environment)))
	(input-port/discard-char port)
	(let ((string (input-port/read-string port delimiters)))
	  (input-port/discard-char port)
	  string)))))

(define parser-table/index
  (let ((table (parser-table/copy system-global-parser-table)))
    (parser-table/set-entry! table #\" parse-string)
    table))

(define (massage-sort-key sort-key)
  (let ((sort-key (string-downcase sort-key))
	(magic "\\discretionary {->}{}{->}{}"))
    (let ((index (string-search sort-key magic)))
      (if index
	  (string-append
	   (string-head sort-key index)
	   "->"
	   (string-tail sort-key (+ index (string-length magic))))
	  sort-key))))

(define (write-index pathname entries)
  (call-with-output-file pathname
    (lambda (port)
      (let loop
	  ((entries (delete-duplicates (sort entries page-ref<?) page-ref=?))
	   (previous false))
	(if (not (null? entries))
	    (let ((entry (car entries)))
	      (let ((index
		     (1+ (or (list-position-negative (cdr entries)
			       (lambda (entry*)
				 (and (string=? (page-ref/key entry)
						(page-ref/key entry*))
				      (string=? (page-ref/headcs entry)
						(page-ref/headcs entry*)))))
			     (length (cdr entries))))))
		(if (and previous
			 (char-alphabetic? (string-ref (page-ref/key entry) 0))
			 (not (char=? (string-ref (page-ref/key entry) 0)
				      (string-ref (page-ref/key previous) 0))))
		    (write-string "\\indexspace\n" port))
		(write-index-item (list-head entries index) port)
		(loop (list-tail entries index) entry))))))))

(define (write-index-item entries port)
  (let ((entry (car entries))
	(main-pages
	 (map page-ref/page-number
	      (list-transform-positive entries page-ref/main?))))
    (if (> (length main-pages) 1)
	(warn "multiple main entries" (page-ref/key entry)))
    (let ((aux-pages
	   (map page-ref/page-number
		(list-transform-negative entries
		  (lambda (entry)
		    (or (page-ref/main? entry)
			(list-search-positive main-pages
			  (lambda (n)
			    (= (page-ref/page-number entry) n)))))))))
      (write-string "\\item{\\" port)
      (write-string (page-ref/headcs entry) port)
      (write-string "{" port)
      (write-string (page-ref/key entry) port)
      (write-string "}}{\\hskip .75em}" port)
      (write-comma-separated-list main-pages port)
      (if (and (not (null? main-pages))
	       (not (null? aux-pages)))
	  (write-string "; " port))
      (write-comma-separated-list aux-pages port)
      (newline port))))

(define (write-comma-separated-list items port)
  (if (not (null? items))
      (begin
	(write (car items) port)
	(let loop ((items (cdr items)))
	  (if (not (null? items))
	      (begin
		(write-string ", " port)
		(write (car items) port)
		(loop (cdr items))))))))

(define-structure (page-reference (conc-name page-ref/))
  key
  headcs
  type
  page-number)

(define (page-ref/main? page-ref)
  (eq? (page-ref/type page-ref) 'main))

(define (page-ref<? x y)
  (or (string<? (page-ref/key x) (page-ref/key y))
      (and (string=? (page-ref/key x) (page-ref/key y))
	   (or (string<? (page-ref/headcs x) (page-ref/headcs y))
	       (and (string=? (page-ref/headcs x) (page-ref/headcs y))
		    (or (and (eq? (page-ref/type x) 'aux)
			     (eq? (page-ref/type y) 'main))
			(and (eq? (page-ref/type x) (page-ref/type y))
			     (< (page-ref/page-number x)
				(page-ref/page-number y)))))))))

(define (page-ref=? x y)
  (and (string=? (page-ref/key x) (page-ref/key y))
       (string=? (page-ref/headcs x) (page-ref/headcs y))
       (eq? (page-ref/type x) (page-ref/type y))
       (= (page-ref/page-number x) (page-ref/page-number y))))

(define (string-search string1 string2)
  (let ((char (string-ref string2 0))
	(end1 (string-length string1))
	(end2 (string-length string2)))
    (let ((limit (- end1 (-1+ (string-length string2)))))
      (and (positive? limit)
	   (let loop ((start 0))
	     (let ((index (substring-find-next-char string1 start limit char)))
	       (and index
		    (if (= (substring-match-forward string1 index end1
						    string2 0 end2)
			   end2)
			index
			(loop (1+ index))))))))))

(define (delete-duplicates items equal?)
  (let loop ((items items) (set '()))
    (if (null? items)
	(reverse! set)
	(loop (cdr items)
	      (if (list-search-positive set
		    (lambda (item)
		      (equal? item (car items))))
		  set
		  (cons (car items) set))))))

(define (list-position-negative items predicate)
  (let loop ((items items) (index 0))
    (and (not (null? items))
	 (if (predicate (car items))
	     (loop (cdr items) (1+ index))
	     index))))