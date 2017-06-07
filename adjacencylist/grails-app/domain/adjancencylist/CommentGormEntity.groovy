package adjancencylist

import grails.compiler.GrailsCompileStatic
import demo.Comment

@GrailsCompileStatic
class CommentGormEntity implements Comment {
    String comment
    String author
    CommentGormEntity parent

    static mapping = {
        table 'comment'
        id column: 'comment_id'
        version false
        comment type: 'text'
    }

    static constraints = {
        comment nullable: false
        author nullable: false
        parent nullable: true
    }

    String toString() {
        "${author}: $comment "
    }
}