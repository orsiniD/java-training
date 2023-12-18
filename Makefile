.PHONY: all clean
all:
	./mvnw clean package

clean:
	$(RM) *.class﻿
