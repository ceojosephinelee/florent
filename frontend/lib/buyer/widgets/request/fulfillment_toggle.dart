import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/typography.dart';

class FulfillmentToggle extends StatelessWidget {
  const FulfillmentToggle({
    super.key,
    required this.isPickup,
    required this.onPickup,
    required this.onDelivery,
  });

  final bool isPickup;
  final VoidCallback onPickup;
  final VoidCallback onDelivery;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        Expanded(
          child: _ToggleButton(
            label: '🏪 직접 픽업',
            isSelected: isPickup,
            onTap: onPickup,
          ),
        ),
        const SizedBox(width: 8),
        Expanded(
          child: _ToggleButton(
            label: '🚚 배송 받기',
            isSelected: !isPickup,
            onTap: onDelivery,
          ),
        ),
      ],
    );
  }
}

class _ToggleButton extends StatelessWidget {
  const _ToggleButton({
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
        padding: const EdgeInsets.symmetric(vertical: 12),
        decoration: BoxDecoration(
          color: isSelected ? whiteColor : creamColor,
          borderRadius: BorderRadius.circular(8),
          border: Border.all(
            color: isSelected ? roseColor : borderColor,
            width: 1.5,
          ),
          boxShadow: isSelected
              ? [BoxShadow(color: Colors.black.withValues(alpha: 0.05), blurRadius: 4)]
              : null,
        ),
        alignment: Alignment.center,
        child: Text(
          label,
          style: AppTypography.body(
            fontSize: 13,
            fontWeight: isSelected ? FontWeight.w600 : FontWeight.w400,
            color: isSelected ? roseColor : ink60,
          ),
        ),
      ),
    );
  }
}
