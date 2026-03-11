import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/radius.dart';
import '../../../core/theme/typography.dart';

class TagChip extends StatelessWidget {
  const TagChip({
    super.key,
    required this.label,
    required this.isSelected,
    required this.onTap,
  });

  final String label;
  final bool isSelected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
        decoration: BoxDecoration(
          color: isSelected ? roseLt : creamColor,
          borderRadius: kBorderRadiusSm,
          border: Border.all(
            color: isSelected ? roseColor : borderColor,
            width: 1.5,
          ),
        ),
        child: Text(
          label,
          style: AppTypography.body(
            fontSize: 13,
            fontWeight: isSelected ? FontWeight.w600 : FontWeight.w400,
            color: isSelected ? roseColor : inkColor,
          ),
        ),
      ),
    );
  }
}
