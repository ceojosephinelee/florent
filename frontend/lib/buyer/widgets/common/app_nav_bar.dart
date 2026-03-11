import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/typography.dart';

class AppNavBar extends StatelessWidget {
  const AppNavBar({super.key, required this.title, this.onBack});

  final String title;
  final VoidCallback? onBack;

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 56,
      padding: const EdgeInsets.symmetric(horizontal: 4),
      child: Row(
        children: [
          IconButton(
            onPressed: onBack ?? () => Navigator.of(context).maybePop(),
            icon: const Icon(Icons.arrow_back, size: 22, color: inkColor),
          ),
          const SizedBox(width: 4),
          Text(
            title,
            style: AppTypography.body(fontSize: 16, fontWeight: FontWeight.w600),
          ),
        ],
      ),
    );
  }
}
