import React from 'react';
import { requireNativeComponent, TextInput, StyleSheet } from 'react-native';

const emptyFunctionThatReturnsTrue = () => true;

class PlainText extends TextInput {

	componentDidUpdate( prevProps ) {
		if ( this.props.isSelected && ! prevProps.isSelected ) {
			this._editor.focus();
		} else if ( ! this.props.isSelected && prevProps.isSelected ) {
			this._editor.blur();
		}

		super.componentDidMount( prevProps );
	}

	render() {
		const props = Object.assign( {}, this.props );
		props.style = [ this.props.style ];
		props.style.unshift( styles.multilineInput );

		if ( props.selection && props.selection.end == null ) {
			props.selection = {
				start: props.selection.start,
				end: props.selection.start,
			};
		}

		return (
			<RNPlainText
				ref={ (ref) => {
					this._editor = ref;
					this._setNativeRef( ref );
				} }
				{ ...props }
				onFocus={ this._onFocus }
				onBlur={ this._onBlur }
				onChange={ this._onChange }
				onContentSizeChange={ this.props.onContentSizeChange }
				onSelectionChange={ this._onSelectionChange }
				onTextInput={ this._onTextInput }
				onSelectionChangeShouldSetResponder={ emptyFunctionThatReturnsTrue }
				text={ this._getText() }
				dataDetectorTypes={ this.props.dataDetectorTypes }
				onScroll={ this._onScroll }
			/>
		);
	}
}

const styles = StyleSheet.create({
	multilineInput: {
	  // This default top inset makes RCTMultilineTextInputView seem as close as possible
	  // to single-line RCTSinglelineTextInputView defaults, using the system defaults
	  // of font size 17 and a height of 31 points.
	  paddingTop: 5,
	},
  });

const RNPlainText = requireNativeComponent( 'RNPlainText', PlainText );

export default PlainText;