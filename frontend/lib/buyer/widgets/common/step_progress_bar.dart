import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/typography.dart';

class StepProgressBar extends StatelessWidget {
  const StepProgressBar({
    super.key,
    required this.totalSteps,
    required this.currentStep,
  });

  final int totalSteps;
  final int currentStep;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 8),
      child: Row(
        children: List.generate(totalSteps * 2 - 1, (i) {
          if (i.isOdd) {
            final stepBefore = (i ~/ 2) + 1;
            final isDone = stepBefore < currentStep;
            return Expanded(
              child: Container(
                height: 2,
                color: isDone ? roseColor : borderColor,
              ),
            );
          }
          final step = (i ~/ 2) + 1;
          return _StepDot(
            step: step,
            isDone: step < currentStep,
            isActive: step == currentStep,
          );
        }),
      ),
    );
  }
}

class _StepDot extends StatelessWidget {
  const _StepDot({
    required this.step,
    required this.isDone,
    required this.isActive,
  });

  final int step;
  final bool isDone;
  final bool isActive;

  @override
  Widget build(BuildContext context) {
    Color bg;
    Widget child;

    if (isDone) {
      bg = roseColor;
      child = const Icon(Icons.check, size: 14, color: whiteColor);
    } else if (isActive) {
      bg = inkColor;
      child = Text(
        '$step',
        style: AppTypography.mono(fontSize: 11, color: whiteColor),
      );
    } else {
      bg = borderColor;
      child = Text(
        '$step',
        style: AppTypography.mono(fontSize: 11, color: whiteColor),
      );
    }

    return Container(
      width: 28,
      height: 28,
      decoration: BoxDecoration(shape: BoxShape.circle, color: bg),
      alignment: Alignment.center,
      child: child,
    );
  }
}
