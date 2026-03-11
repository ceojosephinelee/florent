import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import 'colors.dart';

abstract final class AppTypography {
  // Cormorant Garamond — 로고, 타이틀 (영문 세리프)
  static TextStyle serif({
    double fontSize = 24,
    FontWeight fontWeight = FontWeight.w600,
    Color color = inkColor,
  }) {
    return GoogleFonts.cormorantGaramond(
      fontSize: fontSize,
      fontWeight: fontWeight,
      color: color,
    );
  }

  // Noto Sans KR — 한국어 본문
  static TextStyle body({
    double fontSize = 14,
    FontWeight fontWeight = FontWeight.w400,
    Color color = inkColor,
    double? height,
  }) {
    return GoogleFonts.notoSansKr(
      fontSize: fontSize,
      fontWeight: fontWeight,
      color: color,
      height: height,
    );
  }

  // DM Mono — 타이머, 뱃지
  static TextStyle mono({
    double fontSize = 12,
    FontWeight fontWeight = FontWeight.w400,
    Color color = inkColor,
  }) {
    return GoogleFonts.dmMono(
      fontSize: fontSize,
      fontWeight: fontWeight,
      color: color,
    );
  }
}
