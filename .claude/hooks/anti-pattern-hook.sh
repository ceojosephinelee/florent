#!/bin/bash
INPUT=$(cat)
FILE_PATH=$(echo "$INPUT" | python3 -c "import sys,json; d=json.load(sys.stdin); print(d.get('tool_input',{}).get('file_path',''))" 2>/dev/null || echo "")

if [[ -z "$FILE_PATH" || "$FILE_PATH" != *.java ]]; then
  exit 0
fi
if [[ ! -f "$FILE_PATH" ]]; then
  exit 0
fi

ERRORS=""

if grep -n "@Autowired" "$FILE_PATH" | grep -v "//"; then
  ERRORS+="[ERROR] @Autowired 금지 → @RequiredArgsConstructor + private final\n"
fi

if grep -n "^@Data" "$FILE_PATH" | grep -v "//"; then
  ERRORS+="[ERROR] @Data 금지 → record 사용\n"
fi

if grep -n "throw new RuntimeException" "$FILE_PATH" | grep -v "//"; then
  ERRORS+="[ERROR] RuntimeException 금지 → BusinessException 상속\n"
fi

if echo "$FILE_PATH" | grep -q "/domain/"; then
  if grep -n "import jakarta.persistence\|import org.springframework.data" "$FILE_PATH" | grep -v "//"; then
    ERRORS+="[ERROR] Domain 계층에서 JPA 의존 금지\n"
  fi
fi

if [[ -n "$ERRORS" ]]; then
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" >&2
  echo "  Anti-Pattern 감지: $FILE_PATH" >&2
  echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━" >&2
  echo -e "$ERRORS" >&2
  exit 2
fi

exit 0