mini-langtools
==============

A toy language toolset to experiment with lexer / parser / code
generation and virtual machine implementation.

The origin of the project is the mini language implementation provided
by the University of Wien (Austria) for a compiler creation course :
http://www.complang.tuwien.ac.at/ublu/MiniVM.java

One feature that I find useful to grok the code at first is that the
whole code lays in a single .java source. This will hopefully change
in the furture while adding some features and improving the
evolutivity. I may plan to add support for different target VMs and /
or generation of source code for other languages (e.g. generate jasmin
source).

As the code is dating from circa 1999, the java code lacks some more
recent features like the Enum introduced by Java 1.5. I first upgraded
the code to a more modern form before starting to hack it a while.

What I have added to the original code :
- improved the opcode definition into an enum type with extra info on
  each opcode : need for extra byte as argument + opcode description
- improved the generated code listing
- possibility to trace code execution and stack
- added an opcode to print a value
