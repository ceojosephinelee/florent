import 'package:flutter/material.dart';
import 'package:google_fonts/google_fonts.dart';

import 'colors.dart';

ThemeData buyerTheme() {
  return ThemeData(
    useMaterial3: true,
    scaffoldBackgroundColor: creamColor,
    colorScheme: ColorScheme.fromSeed(
      seedColor: roseColor,
      primary: roseColor,
      surface: creamColor,
      onSurface: inkColor,
    ),
    appBarTheme: const AppBarTheme(
      backgroundColor: creamColor,
      surfaceTintColor: Colors.transparent,
      elevation: 0,
    ),
    textTheme: GoogleFonts.notoSansKrTextTheme(),
    dividerTheme: const DividerThemeData(
      color: ink10,
      thickness: 8,
      space: 0,
    ),
    bottomNavigationBarTheme: const BottomNavigationBarThemeData(
      backgroundColor: whiteColor,
      selectedItemColor: roseColor,
      unselectedItemColor: ink30,
      type: BottomNavigationBarType.fixed,
      showUnselectedLabels: true,
    ),
  );
}
