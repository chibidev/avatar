SRC:=src
BIN:=bin

CC:=javac
CFLAGS:=-Xlint -d $(BIN) -sourcepath $(SRC)

PKG:=dev/chibi/avatar
SOURCES:=$(SRC)/$(PKG)/InvalidAvatarException.java $(SRC)/$(PKG)/Avatar.java
OBJ:=$(BIN)/$(PKG)/InvalidAvatarException.class $(BIN)/$(PKG)/Avatar.class

all: clean lib program

clean:
	@if [ -d bin ]; then rm -rf bin; fi

$(BIN)/%.class: $(SRC)/%.java
	@if [ ! -d bin ]; then mkdir bin; fi
	$(CC) $(CFLAGS) $^

$(BIN)/$(PKG)/AvatarReader.class: $(SRC)/$(PKG)/AvatarReader.java
	$(CC) $(CFLAGS) -cp $(BIN)/avatar.jar:. $^

lib: $(OBJ)
	@cd $(BIN); jar cf avatar.jar *

program: lib $(BIN)/$(PKG)/AvatarReader.class
	java -cp bin dev.chibi.avatar.AvatarReader
