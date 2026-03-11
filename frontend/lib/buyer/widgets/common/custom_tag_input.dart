import 'package:flutter/material.dart';

import '../../../core/theme/colors.dart';
import '../../../core/theme/radius.dart';
import '../../../core/theme/typography.dart';

class CustomTagInput extends StatefulWidget {
  const CustomTagInput({
    super.key,
    required this.placeholder,
    required this.onSubmit,
  });

  final String placeholder;
  final void Function(String tag) onSubmit;

  @override
  State<CustomTagInput> createState() => _CustomTagInputState();
}

class _CustomTagInputState extends State<CustomTagInput> {
  final _controller = TextEditingController();
  final _focusNode = FocusNode();

  void _submit() {
    final text = _controller.text.trim();
    if (text.isEmpty) return;
    widget.onSubmit(text);
    _controller.clear();
    _focusNode.requestFocus();
  }

  @override
  void dispose() {
    _controller.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      height: 42,
      decoration: BoxDecoration(
        color: creamColor,
        borderRadius: kBorderRadiusSm,
        border: Border.all(color: borderColor, width: 1.5),
      ),
      child: Row(
        children: [
          const SizedBox(width: 10),
          Text('✏️', style: TextStyle(fontSize: 13)),
          const SizedBox(width: 6),
          Expanded(
            child: TextField(
              controller: _controller,
              focusNode: _focusNode,
              style: AppTypography.body(fontSize: 12),
              decoration: InputDecoration(
                hintText: widget.placeholder,
                hintStyle: AppTypography.body(fontSize: 12, color: ink30),
                border: InputBorder.none,
                isDense: true,
                contentPadding: const EdgeInsets.symmetric(vertical: 10),
              ),
              onSubmitted: (_) => _submit(),
            ),
          ),
          GestureDetector(
            onTap: _submit,
            child: Container(
              padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 6),
              margin: const EdgeInsets.only(right: 4),
              decoration: BoxDecoration(
                color: roseColor,
                borderRadius: BorderRadius.circular(6),
              ),
              child: Text(
                '추가',
                style: AppTypography.body(
                  fontSize: 11,
                  fontWeight: FontWeight.w600,
                  color: whiteColor,
                ),
              ),
            ),
          ),
        ],
      ),
    );
  }
}
