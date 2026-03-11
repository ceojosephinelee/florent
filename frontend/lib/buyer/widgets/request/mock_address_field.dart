import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/radius.dart';
import '../../../core/theme/typography.dart';

class MockAddressField extends StatelessWidget {
  const MockAddressField({
    super.key,
    this.value,
    required this.placeholder,
    required this.onSelected,
  });

  final String? value;
  final String placeholder;
  final void Function(String address) onSelected;

  @override
  Widget build(BuildContext context) {
    final hasValue = value != null && value!.isNotEmpty;

    return GestureDetector(
      onTap: () {
        // Mock: 주소 검색 대신 하드코딩 주소 선택
        onSelected('서울 강남구 역삼동 123-45');
      },
      child: Container(
        width: double.infinity,
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 13),
        decoration: BoxDecoration(
          color: creamColor,
          borderRadius: kBorderRadiusSm,
          border: Border.all(
            color: hasValue ? ink30 : borderColor,
            width: 1.5,
          ),
        ),
        child: Row(
          children: [
            Expanded(
              child: Text(
                hasValue ? value! : placeholder,
                style: AppTypography.body(
                  fontSize: 13,
                  color: hasValue ? inkColor : ink30,
                ),
              ),
            ),
            const Text('🔍', style: TextStyle(fontSize: 15)),
          ],
        ),
      ),
    );
  }
}
