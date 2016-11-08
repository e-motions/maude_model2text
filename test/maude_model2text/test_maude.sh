#!/bin/bash

MAUDEBIN="maude27 -no-banner -no-advise"

echo "
Testing model: Production Line System";
echo "-------------------------------------";

echo " --> New output"
TEMP_PLS_TEST=out_testing_pls.outmaude
$MAUDEBIN maude/pls/TickModule_testing.maude &> $TEMP_PLS_TEST
cat $TEMP_PLS_TEST
rm $TEMP_PLS_TEST

echo " --> Legacy output"
TEMP_PLS=out_legacy_pls.outmaude
$MAUDEBIN maude/pls/TickModule_legacy.maude &> $TEMP_PLS
cat $TEMP_PLS
rm $TEMP_PLS
