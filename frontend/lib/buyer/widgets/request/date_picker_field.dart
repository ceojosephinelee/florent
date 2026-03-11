import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/radius.dart';
import '../../../core/theme/typography.dart';

class DatePickerField extends StatelessWidget {
  const DatePickerField({
    super.key,
    this.value,
    required this.onSelected,
  });

  final String? value;
  final void Function(String isoDate) onSelected;

  String _formatDisplay(String isoDate) {
    final date = DateTime.parse(isoDate);
    final weekday = ['월', '화', '수', '목', '금', '토', '일'];
    return '${date.year}년 ${date.month}월 ${date.day}일 (${weekday[date.weekday - 1]})';
  }

  @override
  Widget build(BuildContext context) {
    final hasValue = value != null;

    return GestureDetector(
      onTap: () async {
        final picked = await showDatePicker(
          context: context,
          initialDate: DateTime.now().add(const Duration(days: 2)),
          firstDate: DateTime.now(),
          lastDate: DateTime.now().add(const Duration(days: 30)),
          builder: (context, child) {
            return Theme(
              data: Theme.of(context).copyWith(
                colorScheme: Theme.of(context).colorScheme.copyWith(
                      primary: roseColor,
                    ),
              ),
              child: child!,
            );
          },
        );
        if (picked != null) {
          onSelected(DateFormat('yyyy-MM-dd').format(picked));
        }
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
                hasValue ? _formatDisplay(value!) : '날짜를 선택해주세요',
                style: AppTypography.body(
                  fontSize: 13,
                  color: hasValue ? inkColor : ink30,
                ),
              ),
            ),
            const Text('📅', style: TextStyle(fontSize: 15)),
          ],
        ),
      ),
    );
  }
}
