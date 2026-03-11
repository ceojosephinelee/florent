import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/radius.dart';
import '../../../core/theme/typography.dart';

class BottomCtaButton extends StatelessWidget {
  const BottomCtaButton({
    super.key,
    required this.label,
    required this.onPressed,
    this.enabled = true,
  });

  final String label;
  final VoidCallback? onPressed;
  final bool enabled;

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      top: false,
      child: Padding(
        padding: const EdgeInsets.fromLTRB(18, 8, 18, 12),
        child: SizedBox(
          width: double.infinity,
          height: 52,
          child: ElevatedButton(
            onPressed: enabled ? onPressed : null,
            style: ElevatedButton.styleFrom(
              backgroundColor: roseColor,
              disabledBackgroundColor: ink30,
              foregroundColor: whiteColor,
              disabledForegroundColor: whiteColor.withValues(alpha: 0.7),
              shape: RoundedRectangleBorder(borderRadius: kBorderRadiusMd),
              elevation: 0,
            ),
            child: Text(
              label,
              style: AppTypography.body(
                fontSize: 15,
                fontWeight: FontWeight.w600,
                color: whiteColor,
              ),
            ),
          ),
        ),
      ),
    );
  }
}
