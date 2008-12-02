SHELL=/bin/bash

ifndef X10C
  X10C = x10c
endif


LANG=\
lang/ArithmeticOps_T.x10\
lang/ComparisonOps_T.x10\
lang/Array_T.x10\
lang/Dist.x10\
lang/IllegalOperationException.x10\
lang/RankMismatchException.x10\
lang/Indexable_T.x10\
lang/Point.x10\
lang/Region.x10\
lang/Settable_T.x10\
lang/T.x10


LANG_GEN=\
gen/Array_Object.x10\
gen/Array_double.x10\
gen/Array_int.x10\
gen/Indexable_double.x10\
gen/Indexable_Object.x10\
gen/Indexable_int.x10\
gen/Settable_Object.x10\
gen/Settable_double.x10\
gen/Settable_int.x10\
gen/ArithmeticOps_Array_Object.x10\
gen/ArithmeticOps_Array_double.x10\
gen/ArithmeticOps_Array_int.x10\
gen/ArithmeticOps_Array_T.x10\
gen/ArithmeticOps_Point.x10\
gen/SetOps_Region.x10\
gen/ComparisonOps_Array_Object.x10\
gen/ComparisonOps_Array_double.x10\
gen/ComparisonOps_Array_int.x10\
gen/ComparisonOps_Array_T.x10\
gen/ComparisonOps_Point.x10

ARRAY_GEN=\
gen/BaseArray_Object.x10\
gen/BaseArray_double.x10\
gen/BaseArray_int.x10\
gen/Array1_Object.x10\
gen/Array1_double.x10\
gen/Array1_int.x10\
gen/ArrayN_Object.x10\
gen/ArrayN_double.x10\
gen/ArrayN_int.x10\
gen/ArrayV_Object.x10\
gen/ArrayV_double.x10\
gen/ArrayV_int.x10

UTIL_GEN=\
gen/Iterator_Halfspace.x10\
gen/Iterator_Scanner.x10\
gen/Iterator_PolyRegion.x10\
gen/Iterator_Region.x10\
gen/Iterator_place.x10\
gen/Map_place_Region.x10\
gen/Set_place.x10\
gen/ArrayList_PolyRegion.x10\
gen/ArrayList_Halfspace.x10\
gen/ArrayList_Region.x10\
gen/ArrayList_place.x10

X10C_OPTS = -BAD_PLACE_RUNTIME_CHECK=false -ARRAY_OPTIMIZATIONS=true

build:
	@cd lang && $(MAKE) -f ../array.mak gen $(LANG_GEN)
	@cd array && $(MAKE) -f ../array.mak gen $(ARRAY_GEN)
	@cd util && $(MAKE) -f ../array.mak gen $(UTIL_GEN)
	$(X10C) -J-Xmx1024m $(X10C_OPTS) */gen/*.x10 $(LANG) array/*.x10

gen:
	mkdir gen



#
# expand as necessary
#

gen/%_double.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1double\2/g" <$< >$@
	@chmod -w $@

gen/%_int.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1int\2/g" <$< >$@
	@chmod -w $@

gen/%_Object.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Object\2/g" <$< >$@
	@chmod -w $@

gen/%_Point.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Point\2/g" <$< >$@
	@chmod -w $@

gen/%_Region.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Region\2/g" <$< >$@
	@chmod -w $@

gen/%_Halfspace.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Halfspace\2/g" <$< >$@
	@chmod -w $@

gen/%_Scanner.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Scanner\2/g" <$< >$@
	@chmod -w $@

gen/%_PolyRegion.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1PolyRegion\2/g" <$< >$@
	@chmod -w $@

gen/%_Halfspace.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Halfspace\2/g" <$< >$@
	@chmod -w $@

gen/%_place.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1place\2/g" <$< >$@
	@chmod -w $@



#
# expand as necessary
#

gen/%_Array_T.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Array_T\2/g" <$< >$@
	@chmod -w $@

gen/%_Array_Object.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Array_Object\2/g" <$< >$@
	@chmod -w $@

gen/%_Array_double.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Array_double\2/g" <$< >$@
	@chmod -w $@

gen/%_Array_int.x10: %_T.x10
	@rm -f $@
	sed "s/\([^a-zA-Z]\)T\([^a-zA-Z]\)/\1Array_int\2/g" <$< >$@
	@chmod -w $@


#
# special cases
#

gen/Map_place_Region.x10: Map_K_V.x10
	@rm -f $@
	sed -e "s/\([^a-zA-Z]\)K\([^a-zA-Z]\)/\1place\2/g" \
            -e "s/\([^a-zA-Z]\)V\([^a-zA-Z]\)/\1Region\2/g" \
            <$< >$@
	@chmod -w $@
